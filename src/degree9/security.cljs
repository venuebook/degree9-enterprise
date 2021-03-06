(ns degree9.security
 (:require
   [degree9.debug :as dbg]
   [feathers.application :as feathers]
   [feathers.authentication :as auth]))

(dbg/defdebug debug "degree9:enterprise:security")

(defn secure-services
 "Takes a feathers app and adds a hook to enforce a valid JWT on every endpoint"
 [app]
 (debug "Secure all app endpoints with JWT")
 (feathers/hooks app
   (clj->js {:before {:all [(auth/authenticate "jwt")]}})))

(defn with-security [app]
  (debug "Loading server security api")
  (-> app
    (secure-services)))

(defn hook-context->jwt
 "Extracts JWT from hook context. Will only work after secure-services hook."
 [context]
 (some-> (js->clj context :keywordize-keys true)
  :params
  :accessToken))
