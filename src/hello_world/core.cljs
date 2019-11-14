(ns hello-world.core
  (:require [reagent.core :as r]
            [react :as react]
            [promesa.core :as p]
            [cljs.core.async :as a]
            [hello-world.workflow :as wf]))

(println "Hello world!")

(defonce service-url "http://39.98.137.244:7777/get-sdk-url/sdk.js")
(defonce scene-url "http://39.98.137.244:7777/demo/scene/scene.json")
(defonce dynamic-material-url "http://39.98.137.244:7777/demo/replace.json")
(defonce demo-texture-url "http://39.98.137.244:7777/demo/fabric/JNS496187.jpg")
(defonce model-urls #js ["http://39.98.137.244:7777/demo/chenyi.json"
                         "http://39.98.137.244:7777/demo/dp.json"
                         "http://39.98.137.244:7777/demo/dp_lz.json"
                         "http://39.98.137.244:7777/demo/dp_2k.json"
                         "http://39.98.137.244:7777/demo/dp_2k_ky.json"
                         "http://39.98.137.244:7777/demo/dp_pbt_7.5.json"
                         "http://39.98.137.244:7777/demo/dp_pbt_7.5_ky.json"
                         "http://39.98.137.244:7777/demo/dp_1k.json"
                         "http://39.98.137.244:7777/demo/dp_1k_ky.json"])

(defn js-fetch [url]
  (-> url
      js/fetch
      (.then #(.json %))))

(defn import-script [src]
  (js/Promise.
   (fn [resolve reject]
     (let [script (.createElement js/document "script")]
       (goog.object/set script "onload" #(resolve js/__3d_sdk__))
       (goog.object/set script "onerror" reject)
       (goog.object/set script "src" src)
       (.appendChild js/document.body script)))))


(defn init-task [state]
  (a/go
    (->> (wf/call! js-fetch service-url) a/<! (swap! state assoc :sdk-url))
    (->> (wf/call! import-script (-> @state :sdk-url .-url)) a/<! (swap! state assoc :sdk))
    (->> (wf/call! (-> (:sdk @state) (.init "3d-container" scene-url)) a/<!))
    (->> (wf/call! (.loadDynamicMaterial (:sdk @state) dynamic-material-url)) a/<!)
    (->> (wf/call! (.updateBaseTexture (:sdk @state) demo-texture-url 4 1)) a/<!)
    (->> (wf/call! (.loadModels (:sdk @state) model-urls)) a/<! (swap! state assoc :models))
    (.forEach (:models @state)
              (fn [m]
                (.showObj (:sdk @state) (.-id m) (.-key m))
                (.freshFabric (:sdk @state) (.-id m) (.-key m))))
    (prn @state)))
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