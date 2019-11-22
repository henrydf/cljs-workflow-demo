(ns hello-world.core
  (:require [hx.react :as hx :refer [defnc]]
            [hx.hooks :as hooks]
            [react-dom :as react-dom]
            [hello-world.workflow :as wf]
            [hello-world.task :as task]))

(defonce service-url "http://39.98.137.244:7777/get-sdk-url/sdk.js")
(defonce scene-url "http://39.98.137.244:7777/demo/scene/scene.json")
(defonce dynamic-material-url "http://39.98.137.244:7777/demo/replace.json")


(defonce models-1 #js ["http://39.98.137.244:7777/demo/chenyi.json"
                       "http://39.98.137.244:7777/demo/dp.json"
                       "http://39.98.137.244:7777/demo/dp_lz.json"
                       "http://39.98.137.244:7777/demo/kd_hxxd.json"
                       "http://39.98.137.244:7777/demo/kouDaiJin.json"
                       "http://39.98.137.244:7777/demo/dp_1k_b.json"
                       "http://39.98.137.244:7777/demo/dp_1k_ky.json"
                       "http://39.98.137.244:7777/demo/dp_2k_b.json"
                       "http://39.98.137.244:7777/demo/dp_2k_ky.json"
                       "http://39.98.137.244:7777/demo/dp_pbt_7.5.json"
                       "http://39.98.137.244:7777/demo/dp_pbt_7.5_ky.json"
                       "http://39.98.137.244:7777/demo/hp_dkc.json"
                       "http://39.98.137.244:7777/demo/hp_dkc_lz.json"
                       "http://39.98.137.244:7777/demo/kd_zckd.json"
                       "http://39.98.137.244:7777/demo/lingDai.json"
                       "http://39.98.137.244:7777/demo/xiuzi.json"
                       "http://39.98.137.244:7777/demo/xiuzi_4kz_b.json"
                       "http://39.98.137.244:7777/demo/xiuzi_4ky.json"
                       "http://39.98.137.244:7777/demo/shadow_plane.json"])
(defonce models-2 #js ["http://39.98.137.244:7777/chenyi/biaoZhunLing.json"
                       "http://39.98.137.244:7777/chenyi/faXiu.json"
                       "http://39.98.137.244:7777/chenyi/kouZi_xiaBai.json"
                       "http://39.98.137.244:7777/chenyi/zhiBai.json"
                       "http://39.98.137.244:7777/chenyi/kouYan_yuanBai.json"
                       "http://39.98.137.244:7777/chenyi/guaBian.json"])

(defnc container []
  (hooks/useEffect #(do
                      (wf/fork! task/init-sdk! {:service-api service-url
                                                :id "3d-container"
                                                :scene scene-url
                                                :material dynamic-material-url})
                      (wf/fork! task/update-models!)
                      (wf/fork! task/update-fabric!)
                      (fn [] (wf/put! {:type :reset})))
                   [])
  
  [:<>
   [:div {:id "3d-container"
          :style {:height "100vh"}}]
   [:div {:style {:position "absolute"
                  :left 0
                  :top 0}}
    [:button {:on-click #(wf/put! {:type :update-fabric
                                   :url "http://39.98.137.244:7777/fabric/xifu/HR10430.jpg"
                                   :repeat 4
                                   :specular 0})} "Fabric 1"]
    [:button {:on-click #(wf/put! {:type :update-fabric
                                   :url "http://cdn.imgs.3vyd.com/fabric/LS-F21A02-19002.jpg"
                                   :repeat 4
                                   :specular 0})} "Fabric 2"]]
   [:div {:style {:position "absolute"
                  :right 0
                  :top 0}}
    [:button {:on-click #(wf/put! {:type :update-models
                                   :models models-1})} "西服"]
    [:button {:on-click #(wf/put! {:type :update-models
                                   :models models-2})} "衬衣"]]])

(react-dom/render
 (hx/f [container])
 (.querySelector js/document "#root"))
