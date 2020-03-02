import Flutter
import UIKit

public class SwiftNativeToastPlugin: NSObject, FlutterPlugin {
  var toastView: UIView?

  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "com.huuang.native_toast", binaryMessenger: registrar.messenger())
    let instance = SwiftNativeToastPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    if ("showToast" == call.method) {
        let argumentsDictionary: Dictionary<String, Any> = call.arguments as! Dictionary<String, Any>
        self.show(message: argumentsDictionary["message"] as! String, position: ToastPosition.init(rawValue: argumentsDictionary["position"] as! Int)!)
        result(0)
    } else {
        result(FlutterMethodNotImplemented)
    }
  }

  func show(message: String, position: ToastPosition) {
      if toastView != nil {
          toastView!.removeFromSuperview()
          NSObject.cancelPreviousPerformRequests(withTarget: self, selector: #selector(hideToast), object: nil)
      }

      let textFont = UIFont.systemFont(ofSize: 17)
      let textWidth = message.size(withAttributes: [.font: textFont]).width
      let window = UIApplication.shared.keyWindow!
      let windowWidth = window.frame.size.width
      let windowHeight = window.frame.size.height
      let toastViewWidth = min(36 + textWidth, windowWidth - 40)
      let options = unsafeBitCast(NSStringDrawingOptions.usesLineFragmentOrigin.rawValue | NSStringDrawingOptions.usesFontLeading.rawValue, to: NSStringDrawingOptions.self)
      let textLabelHeight = (message as NSString).boundingRect(with: CGSize.init(width: windowWidth - 40, height: CGFloat.greatestFiniteMagnitude), options: options, attributes: [NSAttributedString.Key.font: textFont], context: nil).size.height
      let toastViewHeight = CGFloat(textLabelHeight + 16)

      toastView = UIView(frame: CGRect(x: 0, y: 0, width: toastViewWidth, height: toastViewHeight))
      toastView!.center.x = windowWidth / 2
      var y: CGFloat
      switch position {
          case .Top:
              y = windowHeight * 0.25
          case .Center:
              y = windowHeight * 0.5
          case .Bottom:
              y = windowHeight * 0.75
      }
      toastView!.center.y = y

      let backgroundView = UIView(frame: CGRect(x: 0, y: 0, width: toastViewWidth, height: toastViewHeight))
      backgroundView.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.8)
      backgroundView.layer.cornerRadius = 4
      toastView!.addSubview(backgroundView)

      let textLabel = UILabel(frame: CGRect(x: 18, y: 8, width: toastViewWidth - 36, height: textLabelHeight))
      textLabel.text = message
      textLabel.font = textFont
      textLabel.textColor = UIColor(red: 0xfc / 255.0, green: 0xfc / 255.0, blue: 0xfc / 255.0, alpha: 1)
      textLabel.textAlignment = .center
      textLabel.numberOfLines = 0
      toastView!.addSubview(textLabel)

      toastView!.alpha = 0
      window.addSubview(toastView!)
      UIView.animate(withDuration: 0.2) {
          self.toastView!.alpha = 1
      }
      self.perform(#selector(hideToast), with: nil, afterDelay: TimeInterval(max(floor(toastViewHeight / 20), 2)))
  }

  @objc func hideToast() {
      if toastView != nil {
          UIView.animate(withDuration: 0.2, animations: {
              self.toastView!.alpha = 0
          }) { (completition) in
              if self.toastView != nil {
                  self.toastView!.removeFromSuperview()
                  self.toastView = nil
              }
          }
      }
  }
}

enum ToastPosition: Int {
    case Top = 0
    case Center = 1
    case Bottom = 2
}