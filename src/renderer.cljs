(ns renderer
  (:require ["@mui/material/Box" :default Box]
            [ajax.core :refer [POST]]
            [applied-science.js-interop :as j]
            [child_process]
            [cljs-node-io.core :refer [slurp]]
            [com.rpl.specter :as specter]
            [fs]
            [openai :refer [OpenAI]]
            [os]
            [path]
            [reagent.core :as reagent]
            [reagent.dom.client :as client]
            [shadow.cljs.modern :refer [js-await]]
            [stream]
            [yaml]))

(def config
  (-> (path/join (os/homedir) ".config/accent/config.yaml")
      slurp
      yaml/parse
      (js->clj :keywordize-keys true)))

(def openai
  (OpenAI. (clj->js {:apiKey (:openai config)
                     :dangerouslyAllowBrowser true})))

(def sample-rate 16000)

(def temp-directory
  (os/tmpdir))

(def app-temp-directory
  (fs/mkdtempSync (path/join temp-directory "accent-")))

(defn generate-audio-filename []
  (str (random-uuid) ".opus"))

(defn generate-audio-path []
  (path/join app-temp-directory (generate-audio-filename)))

(def url
  "https://api.deepgram.com/v1/listen?model=nova-2&smart_format=true")

(def extract-alternative
  (comp first :alternatives first :channels :results))

(defn calculate-scores [reference-words user-words]
  (js/console.log "Words equal?")
  (js/console.log (= (map :word reference-words) (map :word user-words)))
  (if (= (map :word reference-words) (map :word user-words))
    (map (fn [reference-word user-word]
           (specter/transform :score
                              (fn [score]
                                (- (inc score) (:confidence reference-word)))
                              user-word))
         reference-words
         user-words)
    user-words))

(declare state)

(defn handle-reference-transcription [response]
  (js/console.log "Reference transcription response:")
  (js/console.log response)
  (specter/transform [specter/ATOM :words] (partial calculate-scores (:words (extract-alternative response))) state))

;; The Deepgram JavaScript SDK is not used because it requires a proxy due to CORS restrictions.
;; Even with nodeIntegration enabled, the following error is encountered:
;; "Due to CORS we are unable to support REST-based API calls to our API from the browser.
;; Please consider using a proxy, and including a `restProxy: { url: ''}` in your Deepgram client options."
(defn send-deepgram-request [handler* body]
  (POST url {:handler handler*
             :headers {:Content-Type "audio/*"
                       :Authorization (str "Token " (:deepgram config))}
             :body body
             :response-format :json
             :keywords? true}))

(defn merge-into-atom
  [map* atom*]
  (specter/transform specter/ATOM
                     (fn [value]
                       (merge value map*))
                     atom*))

(defn initialize-score [word]
  (specter/setval :score (dec (:confidence word)) word))

(defn play
  [audio-buffer]
  (let [audio-context (js/AudioContext.)
        source (.createBufferSource audio-context)]
;; https://stackoverflow.com/a/10101213
    (js-await [decoded-data (.decodeAudioData audio-context (.slice audio-buffer 0))]
              (set! (.-buffer source) decoded-data)
              (.connect source (.-destination audio-context))
              (when (:source @state)
                (.stop (:source @state)))
              (.start source)
              (specter/setval [specter/ATOM :source] source state))))

(defn handle-user-transcription [response]
  (js/console.log "User transcription response:")
  (js/console.log response)
  (merge-into-atom (specter/transform :words (partial map initialize-score) (extract-alternative response)) state)
  (js-await [opus (.audio.speech.create openai (clj->js {:model "tts-1"
                                                         :voice "fable"
                                                         :input (:transcript (extract-alternative response))
                                                         :response_format "opus"}))]
            (js/console.log "Generated opus audio")
            (js-await [audio-buffer (.arrayBuffer opus)]
                      (send-deepgram-request handle-reference-transcription (js/Buffer.from audio-buffer))
                      (specter/setval [specter/ATOM :reference] audio-buffer state))))

(defn create-readable []
  (let [readable (stream/Readable. (clj->js {:read (fn [])}))
        filepath (generate-audio-path)
        ffmpeg (child_process/spawn "ffmpeg" (clj->js ["-f" "f32le" "-ar" sample-rate "-i" "pipe:0" "-b:a" "24k" filepath]))]
    (.pipe readable ffmpeg.stdin)
    (.on ffmpeg "close" (fn []
                          (js/console.log "ffmpeg process closed")
                          (send-deepgram-request handle-user-transcription (fs/readFileSync filepath))))
    readable))

(defonce state
  (reagent/atom {:readable (create-readable)}))

(defn push [readable audio]
  (->> audio
       js/Float32Array.
       .-buffer
       js/Buffer.from
       (.push readable)))

(defn reset-readable []
  (specter/setval [specter/ATOM :readable] (create-readable) state))

(defn evaluate []
  (js/console.log "Evaluating pronunciation...")
  (.push (:readable @state) nil)
  (reset-readable))

(defn play-reference []
  (js/console.log "Playing reference speech")
  (play (:reference @state)))

(defn play-user []
  (js/console.log "Playing user speech"))

(defn handle [event]
  (case event.code
    "Space" (evaluate)
    "Escape" (reset-readable)
    "KeyF" (play-reference)
    "KeyD" (play-user)
    "default"))

(defonce root
  (client/create-root (js/document.getElementById "app")))

(defn box []
  [:> Box
   {:display "flex"}
   (map (fn [word]
          ^{:key (:start word)} [:> Box {:display "flex"
                                         :flex-direction "column"
                                         :align-items "center"
                                         :m 1}
                                 [:div (:punctuated_word word)]
                                 [:div (.toFixed (:score word) 2)]])
        (:words @state))])

(defn init []
  (js/console.log "Initializing renderer")
  (js-await [media (js/navigator.mediaDevices.getUserMedia (clj->js {:audio true}))]
            (let [context (js/AudioContext. (clj->js {:sampleRate sample-rate}))]
              (js-await [_ (.audioWorklet.addModule context "audio.js")]
                        (let [processor (js/AudioWorkletNode. context "processor")]
                          (.connect (.createMediaStreamSource context media) processor)
                          (j/assoc-in! processor [:port :onmessage] (fn [message]
                                                                      (push (:readable @state) message.data)))))))
  (set! js/window.onkeydown handle)
  (client/render root [box]))
