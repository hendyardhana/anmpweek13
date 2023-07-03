package com.example.anmp_week13

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import java.lang.Math.sqrt
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometerReading = FloatArray(3)
    private var accelerometerSensor: Sensor? = null

    private var previousV:Float ?= null
    private var stepCount:Int = 0

    private var magneticReading = FloatArray(3)
    private var geomagneticSensor: Sensor? = null
    private var lightSensor: Sensor? = null
    private var proximitySensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        geomagneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        if (accelerometerSensor != null) {
            Toast.makeText(this, "Accelerometer Sensor detected", Toast.LENGTH_SHORT).show()
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST)
        } else {
            Toast.makeText(this, "No Accelerometer sensor detected", Toast.LENGTH_SHORT).show()
        }

        if (geomagneticSensor != null) {
            Toast.makeText(this, "Magnetic Field Sensor detected", Toast.LENGTH_SHORT).show()
            sensorManager.registerListener(this, geomagneticSensor, SensorManager.SENSOR_DELAY_FASTEST)
        } else {
            Toast.makeText(this, "No Magnetic Field Sensor detected", Toast.LENGTH_SHORT).show()
        }

        if (lightSensor != null) {
            Toast.makeText(this, "Light Sensor detected", Toast.LENGTH_SHORT).show()
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_FASTEST)
        } else {
            Toast.makeText(this, "No Light Sensor detected", Toast.LENGTH_SHORT).show()
        }

        if (proximitySensor != null) {
            Toast.makeText(this, "Proximity Sensor detected", Toast.LENGTH_SHORT).show()
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_FASTEST)
        } else {
            Toast.makeText(this, "No ProximitySensor detected", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onSensorChanged(event: SensorEvent?) {
        if(event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            accelerometerReading = event.values
            var x = event.values[0]
            var y = event.values[1]
            var z = event.values[2]
            findViewById<TextView>(R.id.txtAccel).text = "x: $x, y: $y; z: $z"

            var v = sqrt(x.pow(2) + y.pow(2) + z.pow(2))
            if (previousV != null) {
                var dif = v - previousV!!
                if (dif > 6) {
                    stepCount++
                    findViewById<TextView>(R.id.txtStep).text = "$stepCount steps"
                }
            }
            previousV = v
        }

        if(event!!.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticReading = event.values
        }

        if(accelerometerReading != null && magneticReading != null){
            var rotationMatrix = FloatArray(9)
            var orientationAngles = FloatArray(3)

            SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magneticReading)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            var azimuth = (Math.toDegrees(orientationAngles[0].toDouble()) + 360.0) % 360.0
            var pitch = (Math.toDegrees(orientationAngles[1].toDouble()) + 360.0) % 360.0
            var roll = (Math.toDegrees(orientationAngles[2].toDouble()) + 360.0)%360.0

            findViewById<TextView>(R.id.txtGyro).text = "Az=" + (azimuth*100)/100 + "\nPitch=" + (pitch*100)/100 + "\nRoll=" + (roll*100)/100

        }

        if(event.sensor.type ==Sensor.TYPE_LIGHT) {
            findViewById<TextView>(R.id.txtLight).text = event.values[0].toString()
        }

        if (event!!.sensor.type == Sensor.TYPE_PROXIMITY) {
            findViewById<TextView>(R.id.txtProx).text = event.values[0].toString()
            if(event.values[0] <= 0.0) {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}