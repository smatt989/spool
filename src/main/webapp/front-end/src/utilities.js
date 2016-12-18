
var _ = require('lodash');

export function markerByKey(key, markers) {
     return _.find(markers, function(o){return o.key === key});
}

export function showProps(obj){
   for(var key in obj){
       console.log(key+": "+obj[key]);
   }
}

export function triggerByKey(key, triggers){
    return _.find(triggers, function(o){return o.key === key});
}