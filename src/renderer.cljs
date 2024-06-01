(ns renderer
  (:require [applied-science.js-interop :as j]
            [shadow.cljs.modern :refer [js-await]]))

(def sample-rate 16000)

(defn init []
  (js/console.log "Initializing renderer")
  (js-await [media (js/navigator.mediaDevices.getUserMedia (clj->js {:audio true}))]
            (let [context (js/AudioContext. (clj->js {:sampleRate sample-rate}))]
              (js-await [_ (.audioWorklet.addModule context "audio.js")]
                        (let [processor (js/AudioWorkletNode. context "processor")]
                          (.connect (.createMediaStreamSource context media) processor)
                          (j/assoc-in! processor [:port :onmessage] (fn [message])))))))
