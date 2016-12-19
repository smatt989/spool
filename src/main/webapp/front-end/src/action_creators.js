import axios from 'axios';

const fullstack = false;
const domain = fullstack ? "" : "http://localhost:8080";

const mapzen = "https://search.mapzen.com/v1/search";
const mapzen_api_key = "mapzen-d5Qip2m";

export function setState(state) {
    return {
        type: 'SET_STATE',
        state: state
    };
}

export function setLatLng(lat, lng){
    return {
        type: 'SET_LAT_LNG',
        lat: lat,
        lng: lng
    }
}

export function createMarker(latlng, title){
    return {
        type: 'CREATE_MARKER',
        latlng: latlng,
        title: title
    }
}

export function removeMarker(key){
    return {
        type: 'REMOVE_MARKER',
        key: key
    }
}

export function updateMarker(marker){
    return {
        type: 'UPDATE_MARKER',
        marker: marker
    }
}

export function forceMapCenter(){
    return {
        type: 'FORCE_MAP_CENTER'
    }
}

export function mapCentered() {
    return {
        type: 'MAP_CENTERED'
    }
}

export function mapJumpTo(latlng, locationType) {
    return {
        type: 'MAP_JUMP_TO',
        latlng: latlng,
        locationType: locationType
    }
}

export function mapJumped(){
    return {
        type: 'MAP_JUMPED'
    }
}

export function searchRemoteLocations(query){
    const request = axios({
        method: 'get',
        url: `${mapzen}?api_key=${mapzen_api_key}&text=${query}`,
        header: []
    });

    return {
        type: 'SEARCH_REMOTE_LOCATIONS',
        payload: request
    }
}

export function searchRemoteLocationsSuccess(cities){
    return {
        type: 'SEARCH_REMOTE_LOCATIONS_SUCCESS',
        payload: cities
    }
}

export function searchRemoteLocationsError(error){
    return {
        type: 'SEARCH_REMOTE_LOCATIONS_SUCCESS',
        payload: error
    }
}

export function clearRemoteLocationSearchResults(){
    return {
        type: 'CLEAR_REMOTE_LOCATION_SEARCH_RESULTS'
    }
}

export function selectMarker(key){
    return {
        type: 'SELECT_MARKER',
        key: key
    }
}

export function deselectMarker(){
    return {
        type: 'DESELECT_MARKER'
    }
}

export function stageMarkerForEdit(key){
    return {
        type: 'STAGE_MARKER_FOR_EDIT',
        key
    }
}

export function unstageMarkerForEdit(){
    return {
        type: 'UNSTAGE_MARKER_FOR_EDIT'
    }
}

export function createTrigger(title){
    return {
        type: 'CREATE_TRIGGER',
        title: title
    }
}

export function removeTrigger(key){
    return {
        type: 'REMOVE_TRIGGER',
        key: key
    }
}

export function updateTrigger(trigger){
    return {
        type: 'UPDATE_TRIGGER',
        trigger: trigger
    }
}

export function stageTriggerForEdit(trigger){
    return {
        type: 'STAGE_TRIGGER_FOR_EDIT',
        trigger: trigger
    }
}

export function unstageTriggerForEdit(){
    return {
        type: 'UNSTAGE_TRIGGER_FOR_EDIT'
    }
}

export function addActionToStagedTrigger(){
    return {
        type: 'ADD_ACTION_TO_STAGED_TRIGGER'
    }
}

export function updateTitleOfStagedTrigger(title){
    return {
        type: 'UPDATE_TITLE_OF_STAGED_TRIGGER',
        title: title
    }
}

export function updateItemOfStagedTrigger(itemType, itemSubTypeId, key){
    return {
        type: 'UPDATE_ITEM_OF_STAGED_TRIGGER',
        itemType: itemType,
        itemSubTypeId: parseInt(itemSubTypeId),
        key: key
    }
}

export function removeActionFromStagedTrigger(key){
    return {
        type: 'REMOVE_ACTION_FROM_STAGED_TRIGGER',
        key: key
    }
}

export function updateItemVariableAssignmentInStagedTrigger(itemType, itemKey, variableIndex, arrayIndex, assignment){
    return {
        type: 'UPDATE_ITEM_VARIABLE_ASSIGNMENT_IN_STAGED_TRIGGER',
        itemType: itemType,
        itemKey: itemKey,
        variableIndex: variableIndex,
        arrayIndex: arrayIndex,
        assignment: assignment
    }
}

export function removeItemVariableAssignmentInStagedTrigger(itemType, itemKey, variableIndex, arrayIndex, assignment){
    return {
        type: 'REMOVE_ITEM_VARIABLE_ASSIGNMENT_IN_STAGED_TRIGGER',
        itemType: itemType,
        itemKey: itemKey,
        variableIndex: variableIndex,
        arrayIndex: arrayIndex
    }
}

export function addItemVariableAssignmentSlotInStagedTrigger(itemType, itemKey, variableIndex){
    return {
        type: 'ADD_ITEM_VARIABLE_ASSIGNMENT_SLOT_IN_STAGED_TRIGGER',
        itemType: itemType,
        itemKey: itemKey,
        variableIndex: variableIndex
    }
}