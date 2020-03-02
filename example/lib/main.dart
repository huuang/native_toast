import 'package:flutter/material.dart';
import 'package:native_toast/native_toast.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  Widget _buildButton(ToastPosition position) {
    return FlatButton(
      onPressed: () {
        NativeToast.showToast("This is a $position toast.", position: position);
      },
      child: Container(
        alignment: Alignment.center,
        child: Text("Show $position toast"),
      ),
    );
  }

  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            _buildButton(ToastPosition.Top),
            _buildButton(ToastPosition.Center),
            _buildButton(ToastPosition.Bottom),
          ],
        ),
      ),
    );
  }
}
