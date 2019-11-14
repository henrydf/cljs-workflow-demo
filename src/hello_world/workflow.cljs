(ns hello-world.workflow
  (:require [cljs.core.async :as a]))

(def top-chan (a/chan))
(def mult-chan (a/mult top-chan))

(defn take! [wanted-type]
  (let [sub (a/chan)]
    (a/tap mult-chan sub)
    (a/go-loop [action (a/<! sub)]
      (if (= wanted-type (:type action))
        (do
          (a/untap mult-chan sub)
          action)
        (recur (a/<! sub))))))

(defn put! [action]
  (a/put! top-chan action))

(defn fork! [task]
  (task #js {}))

(declare promise-to-chan)
(defn call! [job & args]
  (if (fn? job)
    (let [result (apply job args)]
      (if (instance? js/Promise result)
        (promise-to-chan result)
        result))
    (if (instance? js/Promise job)
      (promise-to-chan job)
      (throw (js/Error. (str "Nothing to do wiht " job ". It should be a promise or return a promise."))))))

(defn promise-to-chan
  "convert a js promise to core.async/chan"
  [p]
  (let [c (a/chan)]
    (-> p
        (.then (fn [result]
                 (a/put! c result)))
        (.catch (fn [error]
                  (a/put! c error))))
    c))




