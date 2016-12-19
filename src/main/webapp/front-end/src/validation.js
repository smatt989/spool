import {Map, List} from 'immutable';
import {itemSubTypeByItemTypeAndId} from './utilities';

export function validateTrigger(trigger, objects){
    const r = triggerHasKey(trigger) && hasOneEvent(trigger) && hasAtLeastOneAction(trigger) && hasValidEvent(trigger, objects) && hasValidActions(trigger, objects)
    if(!r){
        console.log("fail: validateTrigger")
    }
    return r
}

function objectVariableAssignmentIsAValidObject(variableAssignment, objects){
    const set = objects.map(a => a.get('key')).toSet()
    const r = variableAssignment.every(function(a){
        if(!a){
            return false
        }
        return set.includes(a.get('objectKey'))})
    if(!r){
        console.log("fail: objectVariableAssignmentIsAValidObject")
    }
    return r
}

function triggerHasKey(trigger){
    const r = trigger.get('key').length > 0
        if(!r){
            console.log("fail: triggerHasKey")
        }
        return r
}

function hasOneEvent(trigger){
    const r = Map.isMap(trigger.get('event'));
        if(!r){
            console.log("fail: hasOneEvent")
        }
        return r
}

function hasValidEvent(trigger, objects){
    const r = validTriggerItem(trigger.get('event'), "event", objects)
        if(!r){
            console.log("fail: hasValidEvent")
        }
        return r
}

function hasAtLeastOneAction(trigger){
    const r = trigger.get('actions').size >= 1;
        if(!r){
            console.log("fail: hasAtLeastOneAction")
        }
        return r
}

function hasValidActions(trigger, objects){
    const r = trigger.get('actions').every(function(a){return validTriggerItem(a, "action", objects)})
        if(!r){
            console.log("fail: hasValidActions")
        }
        return r
}

function validTriggerItem(item, itemType, objects){
    const itemSubTypeId = item.get('itemSubTypeId');
    const itemSubType = itemSubTypeByItemTypeAndId(itemType, itemSubTypeId)

    if(!itemSubType){
        return false
    }

    const variables = itemSubType.variables ? itemSubType.variables : []
    const variableAssignments = item.get('varAssignments')

    const r = hasValidSubType(itemSubType) && hasRightNumberOfVariables(item, itemType) && hasAllValidVariableAssignments(item, itemType, objects)
    if(!r){
        console.log("fail: validTriggerItem")
    }
    return r
}

function hasValidSubType(subType){
    const r = subType ? true : false
        if(!r){
            console.log("fail: hasValidSubType")
        }
        return r
}

function hasRightNumberOfVariables(item, itemType){
    const itemSubTypeId = item.get('itemSubTypeId');
    const itemSubType = itemSubTypeByItemTypeAndId(itemType, itemSubTypeId)
    var r;
    if(!itemSubType.variables){
        r = item.get('varAssignments').size === 0
    } else {
        r = itemSubType.variables.length === item.get('varAssignments').size
    }
    if(!r){
        console.log("fail: hasRightNumberOfVariables")
    }
    return r
}

function hasAllValidVariableAssignments(item, itemType, objects){
    const itemSubTypeId = item.get('itemSubTypeId');
    const itemSubType = itemSubTypeByItemTypeAndId(itemType, itemSubTypeId)

    const variables = itemSubType.variables ? itemSubType.variables : []
    const variableAssignments = item.get('varAssignments')

    const r = variableAssignments.every(function(va, index){return validVariableAssignment(variables[index], va, objects)})
    if(!r){
        console.log("fail: hasAllValidVariableAssignments")
    }
    return r
}

function validVariableAssignment(variable, variableAssignment, objects){
    var s;
    if(variable.variableType === "object"){
        s = objectVariableAssignmentIsAValidObject(variableAssignment, objects)
    } else {
        s = true;
    }
    const r = variableAssignmentRightArity(variable, variableAssignment) && variableIsAssigned(variableAssignment) && s
    if(!r){
        console.log("fail: validVariableAssignment")
    }
    return r
}

function variableAssignmentRightArity(variable, variableAssignment){
    const arity = variable.variableArity
    var r;
    if(arity === "one"){
        r = variableAssignment.size === 1
    } else if(arity === "array"){
        r = variableAssignment.size >= 1
    } else {
        r = false
    }
        if(!r){
            console.log("fail: variableAssignmentRightArity")
        }
        return r
}

function variableIsAssigned(variableAssignment){
    const r = variableAssignment.every(function(o){return o != null})
        if(!r){
            console.log("fail: variableIsAssigned")
        }
        return r
}