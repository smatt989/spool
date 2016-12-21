var _ = require('lodash');

export function markerByKey(key, markers) {
     return _.find(markers.toArray(), function(o){return o.get('key') === key});
}

export function showProps(obj){
   for(var key in obj){
       console.log(key+": "+obj[key]);
   }
}

export function triggerByKey(key, triggers){
    return _.find(triggers.toArray(), function(o){return o.get('key') === key});
}

export function itemSubTypeByItemTypeAndId(itemType, itemSubTypeId, triggerModel){
    if(itemType === "action"){
        return triggerModel.get('actions').find(function(o){return o.get('id') === itemSubTypeId})
    }else if(itemType === "event"){
        return triggerModel.get('events').find(function(o){return o.get('id') === itemSubTypeId})
    }else {
        console.log("VERY BAD")
    }
}