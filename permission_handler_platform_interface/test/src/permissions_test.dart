import 'package:flutter_test/flutter_test.dart';
import 'package:permission_handler_platform_interface/permission_handler_platform_interface.dart';

void main() {
  test('Permission has the right amount of possible PermissionGroup values',
      () {
    final values = Permission.values;

    expect(values.length, 22);
  });

  test('check if byValue returns corresponding PermissionGroup value', () {
    final values = Permission.values;

    for (var i = 0; i < values.length; i++) {
      expect(values[i], Permission.byValue(i));
    }
  });

  test('check if toString method returns the corresponding name', () {
    var permissionWithService = PermissionWithService.private(0);
    var permissionName = permissionWithService.toString();
    expect(permissionName, 'Permission.calendar');
  });
}
