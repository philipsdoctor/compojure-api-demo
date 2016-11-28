(ns compojure-api-demo.core-test
  (:require [clojure.test :refer :all]
            [compojure-api-demo.core :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [compojure-api-demo.routes :as routes])
  (:import (java.io InputStreamReader File)
           (schema.utils ValidationError)))

(defn api-request
  ([method uri]
   (-> (mock/request method uri)
       (mock/content-type "application/json")))
  ([method uri body]
    (-> (api-request method uri)
        (mock/body (json/generate-string body)))))

(defn parse-json-body [response]
  (json/parse-string (slurp (InputStreamReader. (:body response))) true))

(deftest test-post-bar
  (testing "parameters correctly parsed, validated, and passed"
    (let [app (build-app)
          req (api-request :post "/v1/bar?req-id=tako" {:totalTxBytes 10
                                                        :planId "4123EF"
                                                        :rxPercentage 98.02})
          resp (app req)]
      (is (= 200 (:status resp)))))
  (testing "Not including required keys in the map creates a failure"
    (let [app (build-app)
          req (api-request :post "/v1/bar?req-id=tako" {:rxPercentage 98.02})
          resp (app req)]
      (is (= 400 (:status resp))))))
