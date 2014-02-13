(ns clj-hexapod.core
  (require [serial-port :as serial]))

(serial/list-ports)

(def port (serial/open "/dev/ttyUSB0" 38400))

(defn checksum [v]
  (mod (- 255 (reduce + v)) 256))


(checksum [128 128 128 128])
(checksum [128 128 140 20])


(defn vec->bytes [v]
  (byte-array (map #(-> % (Integer.) (.byteValue) (byte)) v)))


(serial/write port (vec->bytes [255 128 128 140 20 0 0 95]))
(serial/write port (vec->bytes [255 128 128 128 128 0 0 255]))

(serial/close port)
