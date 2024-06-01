(ns main
  (:require [electron :refer [app BrowserWindow]]))

(defn create-window []
  (let [win (BrowserWindow.
             (clj->js
              {:width 800
               :height 600
               :webPreferences {:nodeIntegration true}}))]
    (.loadFile win "public/index.html")))

(defn main []
  (js/console.log "App is ready, initializing...")
  (.on app "ready" create-window))
