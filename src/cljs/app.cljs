(ns app)

(defn get-counter []
  (-> (js/fetch "/api/counter")
      (.then #(.json %))
      (.then #(.-counter %))))

(defn inc-counter []
  (-> (js/fetch "/api/counter" #js {:method "POST"})
      (.then #(.json %))
      (.then #(.-counter %))))

;; This is a silly example, use a framework or something 😅
(defn ^:export init []
  (let [root (js/document.getElementById "root")
        button (js/document.createElement "button")
        text (js/document.createElement "div")]
    (set! (.-innerHTML button) "Click me!")
    (set! (.-innerText text) "Counter:")
    (-> (get-counter)
        (.then #(set! (.-innerHTML text) (str "Counter: " %))))
    (set! (.-onclick button)
          (fn []
            (-> (inc-counter)
                (.then #(set! (.-innerHTML text) (str "Counter: " %))))))
    (.appendChild root button)
    (.appendChild root text)))

