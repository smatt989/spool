import {Map, List} from 'immutable';
import {itemSubTypeByItemTypeAndId} from './utilities';

export function validateTrigger(trigger, objects, triggerModel){
    const r = triggerHasKey(trigger) && hasOneEvent(trigger) && hasAtLeastOneAction(trigger) && hasValidEvent(trigger, objects, triggerModel) && hasValidActions(trigger, objects, triggerModel)
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

function integerVariableAssignmentIsAValidInteger(variableAssignment){
    const r = variableAssignment.every(function(a){
        if(!a){
            return false
        }
        return Number.isInteger(a.get('integerValue'))
    })
    if(!r){
        console.log("fail: integerVariableAssignmentIsAValidInteger")
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

function hasValidEvent(trigger, objects, triggerModel){
    const r = validTriggerItem(trigger.get('event'), "event", objects, triggerModel)
        if(!r){
            console.log("fail: hasValidEvent")
        }
        return r
}

function hasAtLeastOneAction(trigger, triggerModel){
    const r = trigger.get('actions').size >= 1;
        if(!r){
            console.log("fail: hasAtLeastOneAction")
        }
        return r
}

function hasValidActions(trigger, objects, triggerModel){
    const r = trigger.get('actions').every(function(a){return validTriggerItem(a, "action", objects, triggerModel)})
        if(!r){
            console.log("fail: hasValidActions")
        }
        return r
}

function validTriggerItem(item, itemType, objects, triggerModel){
    const itemSubTypeId = item.get('itemSubTypeId');
    const itemSubType = itemSubTypeByItemTypeAndId(itemType, itemSubTypeId, triggerModel)

    if(!itemSubType){
        return false
    }

    const variables = itemSubType.get('variables', List.of())

    const r = hasValidSubType(itemSubType) && hasRightNumberOfVariables(item, itemType, triggerModel) && hasAllValidVariableAssignments(item, itemType, objects, triggerModel)
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

function hasRightNumberOfVariables(item, itemType, triggerModel){
    const itemSubTypeId = item.get('itemSubTypeId');
    const itemSubType = itemSubTypeByItemTypeAndId(itemType, itemSubTypeId, triggerModel)
    const r = itemSubType.get('variables', List.of()).size === item.get('varAssignments', List.of()).size
    if(!r){
        console.log("fail: hasRightNumberOfVariables")
    }
    return r
}

function hasAllValidVariableAssignments(item, itemType, objects, triggerModel){
    const itemSubTypeId = item.get('itemSubTypeId');
    const itemSubType = itemSubTypeByItemTypeAndId(itemType, itemSubTypeId, triggerModel)

    const variables = itemSubType.get('variables', List.of())
    const variableAssignments = item.get('varAssignments', List.of())

    const r = variableAssignments.every(function(va, index){return validVariableAssignment(variables.get(index), va, objects)})
    if(!r){
        console.log("fail: hasAllValidVariableAssignments")
    }
    return r
}

function validVariableAssignment(variable, variableAssignment, objects){
    var s;
    if(variable.get('variableType') === "object"){
        s = objectVariableAssignmentIsAValidObject(variableAssignment.get('varAssignment'), objects)
    } else if(variable.get('variableType') === "integer") {
        s = integerVariableAssignmentIsAValidInteger(variableAssignment.get('varAssignment'));
    }
    const r = variableAssignmentRightArity(variable, variableAssignment) && variableIsAssigned(variableAssignment) && s
    if(!r){
        console.log("fail: validVariableAssignment")
    }
    return r
}

function variableAssignmentRightArity(variable, variableAssignment){
    const arity = variable.get('variableArity')
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