(ns compojure-api-demo.routes
  (:require [compojure.api.sweet :as compojure-api :refer [GET POST PUT DELETE ANY describe]]
            [schema.core :as schema-core]
            [ring.util.http-response :as http-response :refer [ok internal-server-error bad-request not-found content-type unsupported-media-type content-type]]))

(schema-core/defschema BarBody
  {:totalTxBytes schema-core/Num
   :planId schema-core/Str
   :rxPercentage schema-core/Num})

(def app-routes
  (compojure-api/routes
   (compojure-api/context "/v1" []
                          (POST "/foo" []
                                :summary "Does many Foo things."
                                :tags ["Info Operations"]
                                :query-params [req-id :- (describe schema-core/Str "The request-id for tracking")
                                               {timeout :- (describe schema-core/Int "The timeout in seconds before Foo should be considered failed. Default: 200.") 200}]
                                :responses {:default {:schema {}}
                                            200 {:schema {}}}
                                (prn "done")
                                (ok {}))

                          (POST "/bar" []
                                :summary "Mostly it just does Bar."
                                :tags ["Another Tag"]
                                :query-params [req-id :- (describe schema-core/Str "The request-id for tracking.")]
                                :body [barBody (describe BarBody "A map of the state of the action, includes planId to match the action identifier, total bytes and current progress.")]
                                :responses {:default {:schema {}}
                                            200 {:schema {}}}
                                (prn "done")
                                (ok {}))

                          (POST "/baz" {body :body}
                                ;; :middleware [legacy-middleware]
                            (ok {})))))
