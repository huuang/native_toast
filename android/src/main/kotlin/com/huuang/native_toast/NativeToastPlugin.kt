package com.huuang.native_toast

import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Point
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast.*
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.util.regex.Pattern

/** NativeToastPlugin */
public class NativeToastPlugin: FlutterPlugin, MethodCallHandler {
  var flutterPluginBinding: FlutterPlugin.FlutterPluginBinding? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    this.flutterPluginBinding = flutterPluginBinding
    val channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "com.huuang.native_toast")
    channel.setMethodCallHandler(NativeToastPlugin());
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "showToast" -> {
          showToast(call.argument("message")!!, ToastPosition.values()[call.argument("position")!!])
          result.success(0)
      }
      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
  }

  private fun showToast(message: String, position: ToastPosition) {
    if (!isAppForeground()) {
        return
    }

    val context: Context = this.flutterPluginBinding!!.applicationContext
    val paint = Paint()
    paint.textSize = 17f

    val scale = context!!.resources.displayMetrics.density.toInt()
    val p = Pattern.compile("[\u4e00-\u9fa5]")
    val toastLength =
            if (p.matcher(message).find()) {
                if (message.length > 7) {
                    LENGTH_LONG
                } else {
                    LENGTH_SHORT
                }
            } else {
                if (message.length > 28) {
                    LENGTH_LONG
                } else {
                    LENGTH_SHORT
                }
            }
    val toast = makeText(context, "", toastLength)
    when (position) {
        ToastPosition.TOP -> {
            toast.setGravity(Gravity.TOP, 0, getScreenHeight() / 5)
        }
        ToastPosition.CENTER -> {
            toast.setGravity(Gravity.CENTER, 0, 0)
        }
        ToastPosition.BOTTOM -> {
            toast.setGravity(Gravity.BOTTOM, 0, getScreenHeight() / 5)
        }
    }

    var temp = 0
    var lineCount = 0
    val maxWidth = context!!.resources.displayMetrics.widthPixels - 100f
    while (temp < message.length) {
        temp += paint.breakText(message.substring(temp), true, maxWidth, null)
        lineCount++
    }

    val backgroundView = View(context)
    backgroundView.setBackgroundColor(Color.BLACK)
    backgroundView.layoutParams = ViewGroup.LayoutParams((36 + paint.measureText(message).toInt()) * scale, 40 * scale)
    backgroundView.alpha = 0.8f
    backgroundView.outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            outline!!.setRoundRect(0, 0, view!!.width, view.height, 4f * scale)
        }
    }
    backgroundView.clipToOutline = true

    val contentLinearLayout = LinearLayout(context)
    contentLinearLayout.layoutParams = backgroundView.layoutParams
    contentLinearLayout.gravity = Gravity.CENTER
    contentLinearLayout.orientation = LinearLayout.VERTICAL


    val textView = TextView(context)
    textView.text = message
    textView.setTextColor(Color.parseColor("#fcfcfc"))
    textView.textSize = 17f
    textView.gravity = Gravity.CENTER
    textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
    contentLinearLayout.addView(textView)

    val relativeLayout = RelativeLayout(context)
    relativeLayout.addView(backgroundView)
    relativeLayout.addView(contentLinearLayout)
    toast.view = relativeLayout

    toast.show()
  }

  private fun isAppForeground(): Boolean {
      print("===========")
      print(this.flutterPluginBinding!!.applicationContext)
      print(this.flutterPluginBinding!!.applicationContext.getSystemService(Context.ACTIVITY_SERVICE))
    val activityManager: ActivityManager = this.flutterPluginBinding!!.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (appProcess in activityManager.runningAppProcesses) {
        if (appProcess.processName.equals(this.flutterPluginBinding!!.applicationContext.packageName)) {
            return appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        }
    }
    return false
  }

  private fun getScreenHeight(): Int {
    val point = Point()
    (this.flutterPluginBinding!!.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealSize(point)
    return if (point.y > point.x) point.y else point.x;
  }
}

enum class ToastPosition {
    TOP,
    CENTER,
    BOTTOM
}