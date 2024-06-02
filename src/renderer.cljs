(ns renderer
  (:require [applied-science.js-interop :as j]
            [shadow.cljs.modern :refer [js-await]]
            [stream]
            [os]
            [fs]
            [path]
            [child_process]
            [cljs-node-io.core :refer [slurp]]
            [yaml]
            [ajax.core :refer [POST]]
            [openai :refer [OpenAI]]
            [reagent.core :as reagent]
            [com.rpl.specter :as specter]))

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

(defn compare-words [response]
  ;; TODO: Display the pronunciation score in the UI
  (js/console.log (:words (extract-alternative response))))

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

(declare state)

(defn merge-into-atom
  [map* atom*]
  (specter/transform specter/ATOM
                     (fn [value]
                       (merge value map*))
                     atom*))

(defn initialize-score [word]
  (specter/setval :score (- (:confidence word) 1) word))

(defn handler [response]
  (merge-into-atom (specter/transform :words (partial map initialize-score) (extract-alternative response)) state)
  (js-await [opus (.audio.speech.create openai (clj->js {:model "tts-1"
                                                         :voice "alloy"
                                                         :input (:transcript (extract-alternative response))
                                                         :response_format "opus"}))]
            (js-await [audio-buffer (.arrayBuffer opus)]
                      (send-deepgram-request compare-words (js/Buffer.from audio-buffer)))))

(defn create-readable []
  (let [readable (stream/Readable. (clj->js {:read (fn [])}))
        filepath (generate-audio-path)
        ffmpeg (child_process/spawn "ffmpeg" (clj->js ["-f" "f32le" "-ar" sample-rate "-i" "pipe:0" "-b:a" "24k" filepath]))]
    (.pipe readable ffmpeg.stdin)
    (.on ffmpeg "close" (fn []
                          (js/console.log "ffmpeg process closed")
                          (send-deepgram-request handler (fs/readFileSync filepath))))
    readable))

(def state
  (reagent/atom {:readable (create-readable)}))

(defn push [readable audio]
  (->> audio
       js/Float32Array.
       .-buffer
       js/Buffer.from
       (.push readable)))

(defn evaluate []
  (js/console.log "Evaluating pronunciation...")
  (.push (:readable @state) nil))

(defn handle [event]
  (when (= event.code "Space")
    (evaluate)))

(defn init []
  (js/console.log "Initializing renderer")
  (js-await [media (js/navigator.mediaDevices.getUserMedia (clj->js {:audio true}))]
            (let [context (js/AudioContext. (clj->js {:sampleRate sample-rate}))]
              (js-await [_ (.audioWorklet.addModule context "audio.js")]
                        (let [processor (js/AudioWorkletNode. context "processor")]
                          (.connect (.createMediaStreamSource context media) processor)
                          (j/assoc-in! processor [:port :onmessage] (fn [message]
                                                                      (push (:readable @state) message.data)))))))
  (set! js/window.onkeydown handle))