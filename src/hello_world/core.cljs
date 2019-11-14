(ns hello-world.core
  (:require [reagent.core :as r]
            [react :as react]
            [promesa.core :as p]
            [cljs.core.async :as a]
            [hello-world.workflow :as wf]))

(println "Hello world!")

#_(defn use-3d-engine []
  (a/go))

(defn aaa []
  (js/Promise. #(%1 "resolved js promise")))

(defn init-task [state]
  (a/go
    #_(->> (wf/take! :sth-in-futher) a/<! (swap! state assoc :1step))
    (->> (wf/call! aaa) a/<! (swap! state assoc :2step))
    (prn state)))
(wf/fork! init-task)

; (a/go
;   (-> (wf/take! :sth-in-futher)
;       a/<!
;       prn))

; (-> (p/delay 2000)
;     (p/then (fn [] (wf/put! {:type :sth-in-futher
;                              :data 123}))))


(defn threed-container []
  (react/useEffect (fn []
                     (prn "log in effect")
                     js/undefined))
  (r/as-element [:div {:id "3d-container"
                       :style {:height "100vh"}}]))

(r/render [:> threed-container] (.querySelector js/document "#root"))