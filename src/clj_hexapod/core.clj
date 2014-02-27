(ns clj-hexapod.core
  (require [serial-port :as serial]))

(def CENTER 128)


(serial/list-ports)

(def port (serial/open "/dev/ttyUSB0" 38400))

(defn checksum [v]
  (mod (- 255 (reduce + v)) 256))


(checksum [128 128 128 128])
(checksum [128 128 120 128])
(checksum [128 128 2 2])


(defn vec->bytes [v]
  (byte-array (map #(-> % (Integer.) (.byteValue) (byte)) v)))

(defn build-packet [r-vert r-horz l-vert l-horz buttons]
  [255 ;header
   r-vert
   r-horz
   l-vert
   l-horz
   buttons
   0
   (checksum [r-vert r-horz l-vert l-horz buttons])])

(defn send [packet]
  (serial/write port (vec->bytes packet)))

(defn good-range? [speed]
  (and (pos? speed) (>= 100 speed)))


;;values between 129-254
(defn up [speed]
  "joystick up for speed between 1-100"
  (if (good-range? speed)
    (int (+ 129 (* 125 (/ speed 100.0))))
    CENTER))

;;values between 0 and 125
(defn down [speed]
  "joystick down speed between 1-100"
  (if (good-range? speed)
    (int (- 125 (* 125 (/ speed 100.0))))
    CENTER))


;;walk forward Left Vertical 129-254
;;walk backwards Left Vertical 0 - 127
;;walk right Left Horizontal 129 - 254
;;walk left Left Horizontal 0 -127
;;middle at 128

;;turn right Right Horizontal 129-254
;;turn left Right Horizontal 0 - 127
;;Right Vertical always 128
;;middle at 128

;; R3 Ripple Smooth Gait - 8
;; R2 Amble Smooth Gait - 16
;; R1 Ripple Gait - 324

;; L6 Amble Gait  - 1
;; L5 Tripod Gait - Normal - 2
;; L4 Tripod Gait - High Speed - 4


(defn walk-forward [speed]
  "walk forward speed between 1-100"
  (send (build-packet CENTER CENTER (up speed) CENTER 0)))

(defn walk-backwards [speed]
  "walk backwards speed between 1-100"
  (send (build-packet CENTER CENTER (down speed) CENTER 0)))

(defn walk-right [speed]
  "walk right speed between 1-100"
  (send (build-packet CENTER CENTER CENTER (up speed) 0)))

(defn walk-left [speed]
  "walk right speed between 1-100"
  (send (build-packet CENTER CENTER CENTER (down speed) 0)))

(defn turn-right [speed]
  "turn right speed between 1-100"
  (send (build-packet CENTER (up speed) CENTER CENTER 0)))

(defn turn-left [speed]
  "turn left speed between 1-100"
  (send (build-packet CENTER (down speed) CENTER CENTER 0)))

(defn stop []
  "stop hexapod"
  (send (build-packet CENTER CENTER CENTER CENTER 0)))

(walk-forward 10)
(walk-backwards 10)
(walk-right 10)
(walk-left 10)
(turn-right 10)
(turn-left 10)
(stop) 



;;walk right
  
(serial/write port (vec->bytes [255 128 128 140 20 0 0 95]))
(serial/write port (vec->bytes [255 128 128 120 128 0 0 7]))
(serial/write port (vec->bytes [255 128 128 2 128 0 0 7]))
(serial/write port (vec->bytes [255 128 128 128 128 0 0 255]))

(serial/close port)


