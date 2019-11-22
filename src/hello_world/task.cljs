(ns hello-world.task
  (:require [hello-world.workflow :as wf]
            [cljs.core.async :as a]
            [hello-world.util :refer [js-fetch qset! qget!]]))

(defn- import-script [src]
  (js/Promise.
   (fn [resolve reject]
     (let [script (.createElement js/document "script")]
       (goog.object/set script "onload" #(resolve js/__3d_sdk__))
       (goog.object/set script "onerror" reject)
       (goog.object/set script "src" src)
       (.appendChild js/document.body script)))))

(def current-models (atom #js []))

(defn init-sdk! [$ {api :service-api
                    container-id :id
                    scene-url :scene
                    material-url :material}]
  (a/go
    (->> (wf/call! js-fetch api) a/<! (qset! $ :sdk-url))
    (->> (wf/call! import-script (qget! $ :sdk-url :url)) a/<! (qset! $ :sdk))
    (->> (wf/call! (.. $ -sdk (init container-id scene-url)) a/<!))
    (->> (wf/call! (.. $ -sdk (loadDynamicMaterial material-url))) a/<!)
    (wf/put! {:type :sdk-inited
              :sdk (.-sdk $)})
    (wf/put! {:type :update-models
              :models @current-models})
    (->> (wf/take! :reset) a/<!)
    (.. $ -sdk reset)))

(defn update-models! [$]
  (a/go
    (->> (wf/take! :sdk-inited) a/<! :sdk (qset! $ :sdk))
    (loop [{model-urls :models} (a/<! (wf/take! :update-models))]
      (reset! current-models model-urls)
      (->> (wf/call! (.. $ -sdk (loadModels model-urls))) a/<! (qset! $ :models))
      (.. $ -models (forEach (fn [m]
                               (.showObj (.-sdk $) (.-id m) (.-key m))
                               (.freshFabric (.-sdk $) (.-id m) (.-key m)))))
      (recur (a/<! (wf/take! :update-models))))))

(defn update-fabric! [$]
  (a/go
    (->> (wf/take! :sdk-inited) a/<! :sdk (qset! $ :sdk))
    (loop [{fabric-url :url
            repeat :repeat
            specular :specular} (a/<! (wf/take! :update-fabric))]
      (->> (wf/call! (.. $ -sdk (updateBaseTexture fabric-url repeat specular))) a/<!)
      (wf/put! {:type :update-models
                :models @current-models})
      (recur (a/<! (wf/take! :update-fabric))))))
