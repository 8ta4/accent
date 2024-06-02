(ns renderer
  (:require [applied-science.js-interop :as j]
            [shadow.cljs.modern :refer [js-await]]
            [stream]))

(def sample-rate 16000)

(defn evaluate []
  (js/console.log "Evaluating pronunciation..."))

(defn handle [event]
  (when (= event.code "Space")
    (evaluate)))

(def readable
  (atom (stream/Readable. (clj->js {:read (fn [])}))))

(defn push [readable audio]
  (->> audio
       js/Float32Array.
       .-buffer
       js/Buffer.from
       (.push readable)))

(defn init []
  (js/console.log "Initializing renderer")
  (js-await [media (js/navigator.mediaDevices.getUserMedia (clj->js {:audio true}))]
            (let [context (js/AudioContext. (clj->js {:sampleRate sample-rate}))]
              (js-await [_ (.audioWorklet.addModule context "audio.js")]
                        (let [processor (js/AudioWorkletNode. context "processor")]
                          (.connect (.createMediaStreamSource context media) processor)
                          (j/assoc-in! processor [:port :onmessage] (fn [message]
                                                                      (push @readable message.data)))))))
  (set! js/window.onkeydown handle))
