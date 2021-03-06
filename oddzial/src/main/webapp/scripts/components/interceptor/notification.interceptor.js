 'use strict';

angular.module('spedycjaoddzialApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-spedycjaoddzialApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-spedycjaoddzialApp-params')});
                }
                return response;
            }
        };
    });
