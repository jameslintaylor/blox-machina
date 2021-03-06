(ns boh.util
  (:require #?(:clj [clojure.core.async :as a :refer [go]]
               :cljs [cljs.core.async :as a])
            #?(:clj pandect.algo.sha1
               :cljs goog.crypt))
  #?(:cljs (:require-macros [cljs.core.async.macros :refer [go]]))
  #?(:cljs (:import goog.crypt.Sha1)))

(def sha1
  #?(:clj pandect.algo.sha1/sha1
     :cljs (fn [s]
             (let [inst (goog.crypt.Sha1.)
                   bs (goog.crypt/stringToUtf8ByteArray s)]
               (.update inst bs)
               (goog.crypt/byteArrayToHex (.digest inst))))))

(defn concat-sha1
  "Concatenates the stringified clojure data and returns its sha1
  hash."
  [& xs]
  (->> (map pr-str xs)
       (apply str)
       sha1
       keyword))

(defn surject-keys
  "Return the map obtained by transforming all keys in the original map
  by a function `f`."
  [m f & args]
  (reduce-kv (fn [m k v] (assoc m (apply f k args) v)) {} m))

(defn select-containing
  "Select the keys in a map whom's values satisfy `pred`."
  [pred m]
  (into {} (filter (comp pred second)) m))

(defn keys-containing
  "Retrieve a set of keys in a map whom's values satisfy `pred`."
  [pred m]
  (into #{} (comp (filter (comp pred second)) (map first)) m))

(defmacro ^{:style/indent 1}
  do-with [[sym expression] & body]
  `(let [~sym ~expression]
     ~@body
     ~sym))

(defmacro ^{:style/indent 1}
  go-let [bindings & body]
  `(go (let ~bindings
         ~@body)))
