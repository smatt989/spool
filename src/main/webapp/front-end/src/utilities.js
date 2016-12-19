import {triggerModel} from './triggerModel';
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

export function itemSubTypeByItemTypeAndId(itemType, itemSubTypeId){
    console.log("item type: "+itemType)
    if(itemType === "action"){
        return _.find(triggerModel.actions, function(o){return o.id === itemSubTypeId})
    }else if(itemType === "event"){
        return _.find(triggerModel.events, function(o){return o.id === itemSubTypeId})
    }else {
        console.log("VERY BAD")
    }
}