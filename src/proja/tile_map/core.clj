(ns proja.tile-map.core
  (require [proja.utils :as utils])
  (:import (com.badlogic.gdx.graphics.g2d SpriteBatch TextureAtlas$AtlasRegion)))

(defn- create-tile
  [x y texture]
  {;:parent nil
   ;:g nil
   ;:f nil
   :grid-x x
   :grid-y y 
   :passable true
   :move-cost 1
   :texture texture
   })

(defn- create-row [row-num num-of-columns tex-cache]
	(loop [indx 0
	       data []]
	     (if (= (count data) num-of-columns)
	       data
	       (recur
	         (inc indx)
	         (conj data (create-tile row-num indx (let [i (rand-int 10)]
                                                  (cond
                                                    (or (== i 0) (== i 1))
                                                    (:grass-1 tex-cache)

                                                    (== i 2)
                                                    (:grass-2 tex-cache)

                                                    (== i 3)
                                                    (:grass-3 tex-cache)

                                                    (== i 4)
                                                    (:grass-4 tex-cache)

                                                    (>= i 5)
                                                    (:ground-1 tex-cache)
                                                    ))))))))

(defn create-grid [num-rows num-cols tex-cache]
  (vec (map #(create-row % num-cols tex-cache) (range 0 num-rows))))

(defn get-num-rows
  [grid]
  (count grid))

(defn get-num-cols
  [grid]
  (count (first grid)))

(defn get-tile [x y grid]
  {:pre [(some? x) (some? y)]}
  (if (and 
        (< x (get-num-rows grid))
        (>= x 0)
        (< y (get-num-cols grid))
        (>= y 0))
    (nth (nth grid x) y)
    nil))

(defn update-tile [grid x y func]
  {:pre [(some? x) (some? y)]}
  (if (and
        (< x (get-num-rows grid))
        (>= x 0)
        (< y (get-num-cols grid))
        (>= y 0))
    (update-in grid [x y] func)
    nil))

(defn get-north-neighbor
  [x y grid]
  (get-tile x (inc y) grid))

(defn get-south-neighbor
  [x y grid]
  (get-tile x (dec y) grid))

(defn get-east-neighbor
  [x y grid]
  (get-tile (inc x) y grid))

(defn get-west-neighbor
  [x y grid]
  (get-tile (dec x) y grid))

(defn get-nw-neighbor
  [x y grid]
  (get-tile (dec x) (inc y) grid))

(defn get-ne-neighbor
  [x y grid]
  (get-tile (inc x) (inc y) grid))

(defn get-sw-neighbor
  [x y grid]
  (get-tile (dec x) (dec y) grid))

(defn get-se-neighbor
  [x y grid]
  (get-tile (inc x) (dec y) grid))

(defn get-neighbors
 [x y grid]
 (filter #(not (nil? %)) 
   (conj
     [] 
     (get-north-neighbor x y grid)
     (get-east-neighbor x y grid)
     (get-south-neighbor x y grid)
     (get-west-neighbor x y grid)
     (get-nw-neighbor x y grid)
     (get-ne-neighbor x y grid)
     (get-sw-neighbor x y grid)
     (get-se-neighbor x y grid))))

(defn draw-grid [grid batch]
	(loop [g grid]
	 (if (= (count g) 0)
	   nil
	   (do
       (.begin ^SpriteBatch batch)
	     (doall (map 
	             (fn [tile]
	              (let [{x :grid-x, y :grid-y, t :texture} tile]
                  (.draw ^SpriteBatch batch
                         ^TextureAtlas$AtlasRegion t
                         ^float (utils/grid->world x)
                         ^float (utils/grid->world y))))
	             (first g)))
      (.end ^SpriteBatch batch)
	     (recur (rest g))))))