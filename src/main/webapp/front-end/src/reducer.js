import {Map, List} from 'immutable';
import {_} from 'underscore';

const uuidv1 = require('uuid/v1');

function setState(state, newState) {
    return state.merge(newState);
}

function setLatLng(state, lat, lng) {
    return state.set('lat', lat).set('lng', lng);
}

function createMarker(state, latlng, title){
    const marker = {latlng: latlng, key: uuidv1(), title: title};
    const newState = state.set('markers', state.get('markers').concat(marker));
    return newState.set('objectCount', newState.get('objectCount')+1)
}

function removeMarker(state, key){
    const markers = state.get('markers');
    const without = markers.filter(function(o) { return o.key != key; });
    return state.set('markers', without);
}

function updateMarker(state, marker){
    const markers = state.get('markers');

    const newMarkers = markers.map(function(o) {
        if(o.key === marker.key){
            return marker
        } else {
            return o
        }
    });

    return state.set('markers', newMarkers);
}

function forceMapCenter(state){
    return state.set('forceMapCenter', true);
}

function mapCentered(state){
    return state.set('forceMapCenter', false);
}

function mapJumpTo(state, latlng, locationType){
    return state.set('mapJumpTo', {latlng: latlng, locationType: locationType});
}

function mapJumped(state){
    return state.set('mapJumpTo', null);
}

function searchRemoteLocations(state) {
    return state.set('remoteLocationsList', {locations: [], error: null, loading: true});
}

function searchRemoteLocationsSuccess(state, locations) {
    return state.set('remoteLocationsList', {locations: locations, error: null, loading: false});
}

function searchRemoteLocationsError(state, error) {
    return state.set('remoteLocationsList', {locations: [], error: error, loading: false});
}

function clearRemoteLocationSearchResults(state){
    return state.set('remoteLocationsList', {locations: [], error: null, loading: false});
}

function selectMarker(state, key){
    return state.set('selectedMarker', key);
}

function deselectMarker(state){
    return state.set('selectedMarker', null);
}

function stageMarkerForEdit(state, key){
    return state.set('editingMarker', key);
}

function unstageMarkerForEdit(state){
    return state.set('editingMarker', null);
}

function createTrigger(state, title){

    const trigger = {key: uuidv1(), title: title, event: null, actions: []};
    const newState = state.set('triggers', state.get('triggers').concat(trigger));
    return newState.set('triggerCount', newState.get('triggerCount')+1)
}

function removeTrigger(state, key){
    const triggers = state.get('triggers');
    const without = triggers.filter(function(o) { return o.key != key; });
    return state.set('triggers', without);
}

function updateTrigger(state, trigger){
    const triggers = state.get('triggers');

    const newTriggers = triggers.map(function(o) {
        if(o.key === trigger.key){
            return trigger
        } else {
            return o
        }
    });

    return state.set('triggers', newTriggers);
}

function stageTriggerForEdit(state, key){
    return state.set('editingTrigger', key);
}

function unstageTriggerForEdit(state){
    return state.set('editingTrigger', null);
}




export default function(state = Map(), action) {
  switch (action.type) {
  case 'SET_STATE':
    return setState(state, action.state);
  case 'SET_LAT_LNG':
    return setLatLng(state, action.lat, action.lng);
  case 'CREATE_MARKER':
    return createMarker(state, action.latlng, action.title);
  case 'REMOVE_MARKER':
    return removeMarker(state, action.key);
  case 'UPDATE_MARKER':
    return updateMarker(state, action.marker);
  case 'FORCE_MAP_CENTER':
    return forceMapCenter(state);
  case 'MAP_CENTERED':
    return mapCentered(state);
  case 'MAP_JUMP_TO':
    return mapJumpTo(state, action.latlng, action.locationType);
  case 'MAP_JUMPED':
    return mapJumped(state);
  case 'SEARCH_REMOTE_LOCATIONS':
    return searchRemoteLocations(state);
  case 'SEARCH_REMOTE_LOCATIONS_SUCCESS':
    return searchRemoteLocationsSuccess(state, action.payload)
  case 'SEARCH_REMOTE_LOCATIONS_FAILURES':
    return searchRemoteLocationsError(state, action.payload)
  case 'CLEAR_REMOTE_LOCATION_SEARCH_RESULTS':
    return clearRemoteLocationSearchResults(state);
  case 'SELECT_MARKER':
    return selectMarker(state, action.key);
  case 'DESELECT_MARKER':
    return deselectMarker(state);
  case 'STAGE_MARKER_FOR_EDIT':
    return stageMarkerForEdit(state, action.key);
  case 'UNSTAGE_MARKER_FOR_EDIT':
    return unstageMarkerForEdit(state);
  case 'CREATE_TRIGGER':
    return createTrigger(state, action.title);
  case 'REMOVE_TRIGGER':
    return removeTrigger(state, action.key);
  case 'UPDATE_TRIGGER':
    return updateTrigger(state, action.trigger);
  case 'STAGE_TRIGGER_FOR_EDIT':
    return stageTriggerForEdit(state, action.key);
  case 'UNSTAGE_TRIGGER_FOR_EDIT':
    return unstageTriggerForEdit(state);
  }
  return state;
}