(ns degree9.mongodb
  (:require [goog.object :as obj]
            [degree9.env :as env]
            [degree9.debug :as dbg]
            [meta.server :as server]
            ["mongoose" :as mongoose]
            ["feathers-mongoose" :as mongodb]))

(dbg/defdebug debug "degree9:enterprise:mongodb")

;; Mongoose Schema Types ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def SchemaTypes (-> mongoose (obj/get "Schema") (obj/get "Types")))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; MongoDB Functions ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn connect
  "Connect to MongoDB. Converts clj->js internally."
  [conn & [opts]]
  (debug "Connecting to MongoDB" conn opts)
  (.connect mongoose conn (clj->js opts)))

(defn mkconnection
  "Create a connection instance to MongoDB."
  [conn & [opts]]
  (debug "Creating connection instance to MongoDB" conn opts)
  (.createConnection mongoose conn (clj->js opts)))

(defn schema
  "Create a Mongoose Schema. Converts clj->js internally."
  [data & [opts]]
  (debug "Creating Mongoose Schema" data opts)
  (let [schema (obj/get mongoose "Schema")]
    (schema. (clj->js data) (clj->js opts))))

(defn model
  "Create a Mongoose Model."
  ([conn name schema]
   (debug "Creating Mongoose Model '%s' with schema:" name schema)
   (.model conn name schema))
  ([name schema]
   (model mongoose name schema)))

(defn api
  "Create a feathers service backed by MongoDB."
  ([app path opts hooks]
   (debug "Initializing MongoDB collection service with options %s" opts)
   (server/api app path (mongodb opts) hooks))
  ([app path db-model db-schema hooks]
   (api app path #js{:Model (model db-model (schema db-schema))} hooks))
  ([app path db-model db-schema schema-opts hooks]
   (api app path #js{:Model (model db-model (schema db-schema schema-opts))} hooks)))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; D9 Public Functions ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn connect! [& opts]
  (let [user (:username   opts (env/get "MONGODB_USERNAME"))
        pass (:password   opts (env/get "MONGODB_PASSWORD"))
        conn (:connection opts (env/get "MONGODB_CONNECTION"))]
    (connect conn
      {:auth {:user user :password pass}
       :useNewUrlParser true})))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
