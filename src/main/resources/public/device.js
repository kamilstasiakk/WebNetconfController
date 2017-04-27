var app = angular.module("logViewer", ["ngRoute"]);
app.config(function($routeProvider) {
    $routeProvider
        .when("/", {
            templateUrl : "home.html",
        })
        .when("/viewer", {
            templateUrl : "viewer.html",
            controller : "viewerCtrl"
        })
        .when("/contact", {
            templateUrl : "contact.html",
            controller : "contactCtrl"
        });
});
app.controller("viewerCtrl", function ($scope, $http) {

    $http.get("http://localhost:8080/devices/all")
        .then(function (response) {
            $scope.devices = response.data;
        });

    $scope.oName = '';
    $scope.nName = '';
    $scope.ipAddress = '';
    $scope.oUser = '';
    $scope.nUser = '';
    $scope.oPassword = '';
    $scope.nPassword = '';
    $scope.oPort = 22;
    $scope.nPort = 22;

    $scope.edit = true;
    $scope.error = false;
    $scope.incomplete = false;
    $scope.hideform = true;
    $scope.hideConfiguration = true;
    $scope.interfaceConfigurationModeLock = false;


    var addDevice = function () {

        var postObject = new Object();
        postObject.name = $scope.oName;
        postObject.ipAddress = $scope.ipAddress;
        postObject.portNumber = $scope.oPort;
        postObject.userName = $scope.oUser;
        postObject.password = $scope.oPassword;


        $http({
            url: 'http://localhost:8080/devices/add',
            dataType: 'json',
            method: 'POST',
            data: postObject,
            headers: {
                "Content-Type": "application/json"
            }
        }).then(function () {
            $scope.updateDevices()
        });

    };

    var editDevice = function () {

        var postObject = new Object();
        postObject.name = $scope.oName;
        postObject.ipAddress = $scope.ipAddress;
        postObject.newName = $scope.nName;
        postObject.userName = $scope.oUser;
        postObject.newUserName = $scope.nUser;
        postObject.password = $scope.oPassword;
        postObject.newPassword = $scope.nPassword;
        postObject.portNumber = $scope.oPort;
        postObject.newPortNumber = $scope.nPort;
        //var s=JSON.stringify(postObject);

        $http({
            url: 'http://localhost:8080/devices/edit',
            dataType: 'json',
            method: 'POST',
            data: postObject,
            headers: {
                "Content-Type": "application/json"
            }
        });

        $scope.updateDevices();

    };

    $scope.removeItem = function (x) {

        var postObject = new Object();
        postObject.name = $scope.devices[x].name;
        postObject.ipAddress = $scope.devices[x].ipAddress;
        postObject.portNumber = $scope.devices[x].port;
        postObject.userName = $scope.devices[x].userName;
        postObject.password = $scope.devices[x].password;
        $http({
            url: 'http://localhost:8080/devices/delete',
            dataType: 'json',
            method: 'POST',
            data: postObject,
            headers: {
                "Content-Type": "application/json"
            }
        });

        $scope.updateDevices();
    };

    $scope.createDeviceModeOn = function () {
        $scope.edit = true;
        $scope.hideform = false;
        $scope.incomplete = true;
        $scope.hideConfiguration = true;

        $scope.oName = '';
        $scope.ipAddress = '';
        $scope.oPassword = '';
        $scope.oUser = '';
        $scope.oPort = 22;
    };

    $scope.editDeviceModeOn = function (x) {
        $scope.edit = false;
        $scope.hideform = false;
        $scope.hideConfiguration = true;
        $scope.editedDeviceIndex = x;
        $scope.incomplete = true;

        $scope.oName = $scope.devices[x].name;
        $scope.ipAddress = $scope.devices[x].ipAddress;
        $scope.oPassword = $scope.devices[x].password;
        $scope.oUser = $scope.devices[x].userName;
        $scope.oPort = $scope.devices[x].portNumber;

        $scope.nName = $scope.devices[x].name;
        $scope.nPassword = $scope.devices[x].password;
        $scope.nPort = $scope.devices[x].portNumber;
        $scope.nUser =  $scope.devices[x].userName;
    };


    $scope.deviceConfigurationModeOn = function (x) {

        $scope.hideform = true;
        $scope.hideConfiguration = false;
        $scope.showGeneral();
        $scope.configuredDeviceName = $scope.devices[x].name;
        $scope.configuredDeviceIp = $scope.devices[x].ipAddress;


    };

    $scope.interfaceConfigurationModeOn = function (x) {

        $scope.interfaceConfigurationModeLock = true;
        $scope.configuringInterfaceName = $scope.interfaces[x].name;
        $scope.incompleteInterface = true;
    };

    $scope.addRouteModeOn = function (x) {

        $scope.addRouteModeLock = true;
        $scope.incompleteRoute = true;
    };

    $scope.changeHostname = function () {
        var postObject = new Object();
        postObject.ipAddress = $scope.configuredDeviceIp;
        postObject.newHostname = $scope.newHostname;

        $http({
            url: 'http://localhost:8080/devices//configure/hostname',
            dataType: 'json',
            method: 'POST',
            data: postObject,
            headers: {
                "Content-Type": "application/json"
            }
        });
        $scope.newHostname = "";
        $scope.showGeneral();
    };


    $scope.changeInterfaceConfiguration = function () {
        var postObject = new Object();
        postObject.ipAddress = $scope.configuredDeviceIp;
        postObject.name = $scope.configuringInterfaceName;
        postObject.interfaceIpAddress = $scope.newInterfaceIpAddress;
        postObject.netmask = $scope.newInterfaceNetmask;
        if ($scope.interfaceAdministrativeStatus) {
            postObject.status = "up";
        } else {
            postObject.status = "down";
        }

        $http({
            url: 'http://localhost:8080/devices/configure/interface',
            dataType: 'json',
            method: 'POST',
            data: postObject,
            headers: {
                "Content-Type": "application/json"
            }
        });
        $scope.newInterfaceIpAddress = "";
        $scope.newInterfaceNetmask = "";
        $scope.interfaceAdministrativeStatus = false;
        $scope.interfaceConfigurationModeLock = false;
        $scope.updateInterfaces();
    };
    $scope.clearInterfaceAddress = function (x) {
        var postObject = new Object();
        postObject.ipAddress = $scope.configuredDeviceIp;
        postObject.name = $scope.interfaces[x].name;
        postObject.interfaceIpAddress = $scope.interfaces[x].ipAddress;
        postObject.netmask = $scope.interfaces[x].netmask;

        $http({
            url: 'http://localhost:8080/devices/configure/clearInterfaceAddress',
            dataType: 'json',
            method: 'POST',
            data: postObject,
            headers: {
                "Content-Type": "application/json"
            }
        });
        $scope.updateInterfaces();
    };

    $scope.addNewRoute = function () {
        var postObject = new Object();
        postObject.ipAddress = $scope.configuredDeviceIp;
        postObject.destNetworkAddress = $scope.newDestNetIp;
        postObject.netmask = $scope.NewDestNetMask;
        postObject.nextHop = $scope.NewOutInterface;

        $http({
            url: 'http://localhost:8080/devices/configure/addRoute',
            dataType: 'json',
            method: 'POST',
            data: postObject,
            headers: {
                "Content-Type": "application/json"
            }
        });
        $scope.newDestNetIp = "";
        $scope.newDestNetIp = "";
        $scope.NewDestNetMask = "";
        $scope.NewOutInterface = "";
        $scope.addRouteModeLock = false;
        $scope.updateRouteTable();

    };

    $scope.deleteRoute = function (x) {
        var postObject = new Object();
        postObject.ipAddress = $scope.configuredDeviceIp;
        postObject.prefix = $scope.routeTable[x].prefix;
        postObject.outgoingInterface = $scope.routeTable[x].nextHop;
        $http({
            url: 'http://localhost:8080/devices/configure/deleteRoute',
            dataType: 'json',
            method: 'POST',
            data: postObject,
            headers: {
                "Content-Type": "application/json"
            }
        });
        $scope.updateRouteTable();
    };

    $scope.showGeneral = function () {

        $scope.showGeneralLock = true;
        $scope.showInterfaceLock = false;
        $scope.showRouteTableLock = false;
        $http.get("http://localhost:8080/devices/configuration/hostname/" + $scope.configuredDeviceIp + "/")
            .then(function (response) {
                $scope.configuredDeviceHostName = response.data.hostname;
            });

    };
    $scope.showInterfaces = function () {
        $http.get("http://localhost:8080/devices/configuration/interfaces/" + $scope.configuredDeviceIp + "/")
            .then(function (response) {
                $scope.interfaces = response.data.interfaces;
            });
        $scope.showGeneralLock = false;
        $scope.showInterfaceLock = true;
        $scope.showRouteTableLock = false;
        $scope.interfaceConfigurationModeLock = false;
    };
    $scope.showRouteTable = function () {

        $http.get("http://localhost:8080/devices/configuration/routeTable/" + $scope.configuredDeviceIp + "/")
            .then(function (response) {
                $scope.routeTable = response.data.routeTable;
            });
        $scope.showGeneralLock = false;
        $scope.showInterfaceLock = false;
        $scope.showRouteTableLock = true;
        $scope.addRouteModeLock = false;
    };



    $scope.confirm = function () {
        if($scope.edit == true) {
            addDevice();
        } else {
            editDevice();
        }
        $scope.oName = '';
        $scope.nName = '';
        $scope.ipAddress = '';

        $scope.hideform = true;
        $scope.hideConfiguration = true;


    };



    $scope.$watch('oName',function() {$scope.test();});
    $scope.$watch('ipAddress',function() {$scope.test();});
    $scope.$watch('nName',function() {$scope.test();});
    $scope.$watch('oUser',function() {$scope.test();});
    $scope.$watch('nUser',function() {$scope.test();});
    $scope.$watch('oPassword',function() {$scope.test();});
    $scope.$watch('nPassword',function() {$scope.test();});
    $scope.$watch('oPort',function() {$scope.test();});
    $scope.$watch('nPort',function() {$scope.test();});
    $scope.$watch('newHostname', function () {$scope.testGeneral();});
    $scope.$watch('newInterfaceIpAddress',function() {$scope.testInterfaces();});
    $scope.$watch('newInterfaceNetmask', function () {$scope.testInterfaces();});
    $scope.$watch('newDestNetIp', function () {$scope.testRouteTable();});
    $scope.$watch('NewDestNetMask',function() {$scope.testRouteTable();});
    $scope.$watch('NewOutInterface', function () {$scope.testRouteTable();});


    $scope.test = function() {
        $scope.incomplete = false;
        if ($scope.edit) {
            if (!$scope.oName.length ||
                !$scope.oPassword.length ||
                !$scope.oUser.length ||
                !$scope.ipAddress.length) {
                $scope.incomplete = true;
            }
        } else {
            if (!$scope.nName.length ||
                !$scope.nPassword.length ||
                !$scope.nUser.length ) {
                $scope.incomplete = true;
            }
        }
    };

    $scope.testGeneral = function() {
        $scope.incompleteGeneral = false;
        if ($scope.showGeneralLock) {
            if (!$scope.newHostname.length) {
                $scope.incompleteGeneral = true;
            }
        }
    };

    $scope.testInterfaces = function() {
        $scope.incompleteInterface = false;
        if ($scope.interfaceConfigurationModeLock) {
            if (!$scope.newInterfaceIpAddress.length ||
                !$scope.newInterfaceNetmask.length) {
                $scope.incompleteInterface = true;
            }
        }
    };

    $scope.testRouteTable = function() {
        $scope.incompleteRoute = false;
        if ($scope.addRouteModeLock) {
            if (!$scope.newDestNetIp.length ||
                !$scope.NewDestNetMask.length ||
                !$scope.NewOutInterface.length ) {
                $scope.incompleteRoute = true;
            }
        }
    };


    $scope.updateDevices = function () {
        $http.get("http://localhost:8080/devices/all")
            .then(function (response) {
                $scope.devices = response.data;
            });

    };

    $scope.updateHostname = function () {
        $http.get("http://localhost:8080/devices/configuration/hostname/" + $scope.configuredDeviceIp + "/")
            .then(function (response) {
                $scope.configuredDeviceHostName = response.data.hostname;
            });

    };


    $scope.updateRouteTable = function () {
        $http.get("http://localhost:8080/devices/configuration/routeTable/" + $scope.configuredDeviceIp + "/")
            .then(function (response) {
                $scope.routeTable = response.data.routeTable;
            });
    };

    $scope.updateInterfaces = function () {
        $http.get("http://localhost:8080/devices/configuration/interfaces/" + $scope.configuredDeviceIp + "/")
            .then(function (response) {
                $scope.interfaces = response.data.interfaces;
            });
    };

    $scope.backToDevices = function () {
      $scope.hideConfiguration = true;
      $scope.updateDevices();
    };




});
app.controller("contactCtrl", function ($scope, $http) {
    $scope.msg = "I love Paris";
});






