describe('PermissionHelper', function() {

  beforeEach(module('riot'));

  it('should check simple permissions', inject(function(PermissionHelper) {
    expect(PermissionHelper.checkPermission("thing", "thing")).toBe(true);
    expect(PermissionHelper.checkPermission("Thing", "thing")).toBe(true);
    expect(PermissionHelper.checkPermission("thing", "Thing")).toBe(true);
  }));
  
  it('should check wildcard permissions', inject(function(PermissionHelper) {
    expect(PermissionHelper.checkPermission("*", "thing")).toBe(true);
	expect(PermissionHelper.checkPermission("thing", "*")).toBe(false);
    expect(PermissionHelper.checkPermission("thing:*", "thing:write")).toBe(true);
    expect(PermissionHelper.checkPermission("thing:*", "thing:read,write")).toBe(true);
  }));
  
  it('should check permissions', inject(function(PermissionHelper) {
    expect(PermissionHelper.checkPermission("thing:write", "thing:write")).toBe(true);
    expect(PermissionHelper.checkPermission("thing:read,write", "thing:write")).toBe(true);	   
    expect(PermissionHelper.checkPermission("thing:read", "thing:write")).toBe(false);
    expect(PermissionHelper.checkPermission("thing:read", "thing:read,write")).toBe(false);
  }));
  
  it('should check complex permissions', inject(function(PermissionHelper) {
    expect(PermissionHelper.checkPermission("thing:read,write:a:*:b:*", "thing:write:a:1:b:2")).toBe(true);	
  }));
  
});
