(ns hello-world.util)

(defn qset
  "allow set value with :keyword. it will ignore : prefix."
  [target key value]
  (goog.object/set target (name key) value))

(defn qget
  "get value by goog.object/getValueByKeys. it will ignore : prefix in keyword."
  [target & keys]
  (let [final-keys (mapv #(if (keyword? %)
                            (name %)
                            %)
                         keys)]
    (apply goog.object/getValueByKeys target final-keys)))