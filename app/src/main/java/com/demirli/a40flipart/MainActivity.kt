package com.demirli.a40flipart

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var clickedButtonId: Int? = null

    private var animationImageMap: TreeMap<Int, Bitmap>? = null

    private lateinit var runnableForAnimation: Runnable
    private lateinit var handlerForAnimation: Handler

    private var transitionSpeed: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        runnableForAnimation = Runnable {}
        handlerForAnimation = Handler()

        animationImageMap = TreeMap()

        val buttonList = listOf(
            btn_1,btn_2,btn_3,btn_4,btn_5,btn_6,btn_7,btn_8)

        addOrRemovePhotoClickListener(buttonList)

        getTransitionSpeed()

        start_btn.setOnClickListener {

            handlerForAnimation.removeCallbacks(runnableForAnimation)

            startAnimation(animationImageMap?.values!!.toList(),transitionSpeed)
        }
    }

    fun addOrRemovePhotoClickListener(buttonList: List<Button>){

        buttonList.forEach {

            it.setOnClickListener {

                val clickedButton = findViewById<Button>(it.id)

                if(clickedButton.text.toString() == "+"){

                    checkPermissionForGalleryResult()
                    clickedButtonId = it.id

                }else if(clickedButton.text.toString() == "-"){
                    removeImage(clickedButton)
                    animationImageMap?.remove(clickedButton.id)
                }
            }
        }
    }


    fun checkPermissionForGalleryResult(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }else{
            intentToGallery()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if(requestCode == 1){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                intentToGallery()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun intentToGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){
            val selectedPicture = data.data

            if(Build.VERSION.SDK_INT >= 28){
                val source = ImageDecoder.createSource(this.contentResolver,selectedPicture!!)
                val selectedBitmap = ImageDecoder.decodeBitmap(source)
                setImage(selectedBitmap)
                animationImageMap?.put(clickedButtonId!!,selectedBitmap)
            }else{
                val selectedBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPicture)
                setImage(selectedBitmap)
                animationImageMap?.put(clickedButtonId!!,selectedBitmap)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun setImage(selectedBitmap: Bitmap){

        val a = findViewById<TextView>(R.id.btn_1)

        when(clickedButtonId){
            btn_1.id -> {image_1.setImageBitmap(selectedBitmap)
                btn_1.setText("-")}
            btn_2.id -> {image_2.setImageBitmap(selectedBitmap)
                btn_2.setText("-")}
            btn_3.id -> {image_3.setImageBitmap(selectedBitmap)
                btn_3.setText("-")}
            btn_4.id -> {image_4.setImageBitmap(selectedBitmap)
                btn_4.setText("-")}
            btn_5.id -> {image_5.setImageBitmap(selectedBitmap)
                btn_5.setText("-")}
            btn_6.id -> {image_6.setImageBitmap(selectedBitmap)
                btn_6.setText("-")}
            btn_7.id -> {image_7.setImageBitmap(selectedBitmap)
                btn_7.setText("-")}
            btn_8.id -> {image_8.setImageBitmap(selectedBitmap)
                btn_8.setText("-")}
        }
    }

    fun removeImage(clickedButton: Button){
        val bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.noimage)
        when(clickedButton){
            btn_1 -> {image_1.setImageBitmap(bitmap)
                btn_1.setText("+")}
            btn_2 -> {image_2.setImageBitmap(bitmap)
                btn_2.setText("+")}
            btn_3 -> {image_3.setImageBitmap(bitmap)
                btn_3.setText("+")}
            btn_4 -> {image_4.setImageBitmap(bitmap)
                btn_4.setText("+")}
            btn_5 -> {image_5.setImageBitmap(bitmap)
                btn_5.setText("+")}
            btn_6 -> {image_6.setImageBitmap(bitmap)
                btn_6.setText("+")}
            btn_7 -> {image_7.setImageBitmap(bitmap)
                btn_7.setText("+")}
            btn_8 -> {image_8.setImageBitmap(bitmap)
                btn_8.setText("+")}
        }
    }

    fun startAnimation(imageBitmapList: List<Bitmap>, transitionSpeed: Long){

        println("Transition Speed: " + transitionSpeed)

        var count = 0
        runnableForAnimation = object : Runnable{

            override fun run() {

                if(imageBitmapList.size > count){
                    animation_imageView.setImageBitmap(imageBitmapList[count])
                    count++
                    if(count == imageBitmapList.size){
                        count = 0
                    }
                }
                handlerForAnimation.postDelayed(runnableForAnimation,transitionSpeed)
            }
        }
        handlerForAnimation.postDelayed(runnableForAnimation,transitionSpeed)
    }

    fun getTransitionSpeed(){

        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                val progressCorrection = 100-progress

                transitionSpeed = progressCorrection.toLong()*10

            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }
}
