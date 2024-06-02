(ns renderer
  (:require [applied-science.js-interop :as j]
            [shadow.cljs.modern :refer [js-await]]
            [stream]
            [os]
            [fs]
            [path]
            [child_process]))

(def sample-rate 16000)

(def temp-directory
  (os/tmpdir))

(def app-temp-directory
  (fs/mkdtempSync (path/join temp-directory "accent-")))

(defn generate-audio-filename []
  (str (random-uuid) ".opus"))

(defn generate-audio-path []
  (path/join app-temp-directory (generate-audio-filename)))

(defn create-readable []
  (let [readable (stream/Readable. (clj->js {:read (fn [])}))
        filepath (generate-audio-path)
        ffmpeg (child_process/spawn "ffmpeg" (clj->js ["-f" "f32le" "-ar" sample-rate "-i" "pipe:0" "-b:a" "24k" filepath]))]
    (.pipe readable ffmpeg.stdin)
    (.on ffmpeg "close" (fn []
                          (js/console.log "ffmpeg process closed")))
    readable))

(def state
  (atom (create-readable)))

(defn push [readable audio]
  (->> audio
       js/Float32Array.
       .-buffer
       js/Buffer.from
       (.push readable)))

(defn evaluate []
  (js/console.log "Evaluating pronunciation...")
  (push @state nil))

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
                                                                      (push @state message.data)))))))
  (set! js/window.onkeydown handle))