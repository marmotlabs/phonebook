(function() {
    'use strict';
    angular
        .module('phonebookApp')
        .factory('Number', Number);

    Number.$inject = ['$resource'];

    function Number ($resource) {
        var resourceUrl =  'api/numbers/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
