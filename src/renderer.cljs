(ns renderer
  (:require [shadow.cljs.modern :refer [js-await]]))

(def sample-rate 16000)

(defn init []
  (js/console.log "Initializing renderer")
  (js/navigator.mediaDevices.getUserMedia (clj->js {:audio true}))
  (let [context (js/AudioContext. (clj->js {:sampleRate sample-rate}))]
    (js-await [_ (.audioWorklet.addModule context "audio.js")]
              (js/AudioWorkletNode. context "processor"))))
