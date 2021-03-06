(ns degree9.auth-test
 (:require
  jwt.examples
  degree9.auth
  meta.client
  degree9.client
  meta.promise
  goog.object
  [javelin.core :as j]
  [cljs.test :refer-macros [deftest is async]]))

; TESTS

#?(:browser
   (do
    (defn ??passes-validation
     [done jwt payload]
     (prn jwt payload)
     (-> (degree9.auth/verify-jwt degree9.client/app jwt)
      (meta.promise/then
       (fn [v]
        (prn v)
        (is
         (= payload (js->clj v :keywordize-keys true))
         "JWT verified but the payload was incorrect")
        (done)))
      (meta.promise/catch
       (fn [v]
        (prn v)
        (is false (str "JWT failed to verify: " jwt))
        (done)))))

    (defn ??fails-validation
     [done jwt message]
     (-> (degree9.auth/verify-jwt degree9.client/app jwt)
      (meta.promise/then
       (fn [v]
        (is false (str "JWT was supposed to fail validation: " (pr-str (js->clj v))))
        (done)))
      (meta.promise/catch
       (fn [v]
        (is (= message (.-message v)))
        (done)))))))

; VALID JWTS

; #?(:browser
;    (deftest ??verify-jwt--valid
;     (async done
;      (??passes-validation
;       done
;       jwt.examples/jwt-valid
;       jwt.examples/jwt-valid-payload))))

; JWT signatures are NOT VALIDATED CLIENT SIDE AT ALL
; #?(:browser
;    (deftest ??verify-jwt--invalid-signature
;     (async done
;      (??passes-validation
;       done
;       jwt.examples/jwt-invalid-signature
;       jwt.examples/jwt-valid-payload))))

; INVALID JWTS

; #?(:browser
;    (do
;     (deftest ??verify-jwt--expired
;      (async done
;       (??fails-validation
;        done
;        jwt.examples/jwt-expired
;        "Invalid token: expired")))
;
;     (deftest ??verify-jwt--not-a-jwt
;      (async done
;       (??fails-validation
;        done
;        jwt.examples/jwt-not-a-jwt
;        "Cannot decode malformed token.")))))
