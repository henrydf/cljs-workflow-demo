(ns hello-world.core
  (:require [reagent.core :as r]
            [react :as react]
            [cljs.core.async :as a]
            [hello-world.workflow :as wf]
            [hello-world.util :refer [qset qget]]))

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


(defn init-task [$]
  (a/go
    (->> (wf/call! js-fetch service-url) a/<! (qset $ :sdk-url))
    (->> (wf/call! import-script (qget $ :sdk-url :url)) a/<! (qset $ :sdk))
    (->> (wf/call! (.. $ -sdk (init "3d-container" scene-url)) a/<!))
    (->> (wf/call! (.. $ -sdk (loadDynamicMaterial dynamic-material-url))) a/<!)
    (->> (wf/call! (.. $ -sdk (updateBaseTexture demo-texture-url 4 0))) a/<!)
    (->> (wf/call! (.. $ -sdk (loadModels model-urls))) a/<! (qset $ :models))
    (.. $ -models (forEach (fn [m]
                             (.showObj (.-sdk $) (.-id m) (.-key m))
                             (.freshFabric (.-sdk $) (.-id m) (.-key m)))))
    (prn $)))
(wf/fork! init-task)

(defn threed-container []
  (react/useEffect (fn []
                     (prn "log in effect")
                     js/undefined))
  (r/as-element [:div {:id "3d-container"
                       :style {:height "100vh"}}]))

(r/render [:> threed-container] (.querySelector js/document "#root"))