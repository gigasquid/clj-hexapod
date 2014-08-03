(ns clj-hexapod.phoenix
  (require [serial-port :as serial]))

(def modes [:walk-mode :translate-mode :rotate-mode :single-leg-mode])
(def LT-button 64)
(def RT-button 128)
(def L6-button 1)
(def L5-button 2)
(def L4-button 4)
(def R3-button 8)
(def R2-button 16)
(def R1-button 32)
(def CENTER 128)
(declare port)


(defn checksum [v]
  (mod (- 255 (reduce + v)) 256))

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

(def command-queue (atom []))
(def default-command (build-packet CENTER CENTER CENTER CENTER 0))

(defn send-commands [packet] (swap! command-queue (repeat 5 packet)))
(defn send-button [packet] (swap! command-queue [packet]))



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

(defn sit-up []
  "raise up to a walking height"
  (send-button (build-packet CENTER CENTER CENTER L5-button 0)))

(defn walk-forward [speed]
  "walk forward speed between 1-100"
  (send-command (build-packet CENTER CENTER (up speed) CENTER 0)))

(defn walk-backwards [speed]
  "walk backwards speed between 1-100"
  (send-command (build-packet CENTER CENTER (down speed) CENTER 0)))

(defn walk-right [speed]
  "walk right speed between 1-100"
  (send-command (build-packet CENTER CENTER CENTER (up speed) 0)))

(defn walk-left [speed]
  "walk right speed between 1-100"
  (send-command (build-packet CENTER CENTER CENTER (down speed) 0)))

(defn turn-right [speed]
  "turn right speed between 1-100"
  (send-command (build-packet CENTER (up speed) CENTER CENTER 0)))

(defn turn-left [speed]
  "turn left speed between 1-100"
  (send-command (build-packet CENTER (down speed) CENTER CENTER 0)))


(defn stop []
  "stop hexapod"
  (send-command (build-packet CENTER CENTER CENTER CENTER 0)))

(defn good-bye []
  (serial/close port))


(comment

  ;; Use this command to see what port your serial port
  ;; is assinged to
  (serial/list-ports)

  ;; replace the USB0 with whater it shows
  (def port (serial/open "/dev/ttyUSB0" 38400))

  (sit-up)
  (walk-forward 20)
  (walk-backwards 10)
  (walk-right 10)
  (walk-left 10)
  (turn-right 10)
  (turn-left 10)
  
  (stop)

  (good-bye)

  )
