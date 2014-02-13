(ns clj-hexapod.core
  (require [serial-port :as serial]))

(serial/list-ports)

(def port (serial/open "/dev/ttyUSB0" 38400))

(defn checksum [v]
  (mod (- 255 (reduce + v)) 256))


(checksum [128 128 128 128])
(checksum [128 128 120 128])
(checksum [128 128 2 2])


(defn vec->bytes [v]
  (byte-array (map #(-> % (Integer.) (.byteValue) (byte)) v)))


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



;;walk right
  
(serial/write port (vec->bytes [255 128 128 140 20 0 0 95]))
(serial/write port (vec->bytes [255 128 128 120 128 0 0 7]))
(serial/write port (vec->bytes [255 128 128 2 128 0 0 7]))
(serial/write port (vec->bytes [255 128 128 128 128 0 0 255]))

(serial/close port)


