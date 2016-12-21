import {Map, List} from 'immutable';
import Immutable from 'immutable';
import {itemSubTypeByItemTypeAndId} from './utilities';

var _ = require('lodash');

const uuidv1 = require('uuid/v1');

function setState(state, newState) {
    return state.merge(newState);
}

function cleanState(){
    const cleanState = Map({
                             id: 0,
                             latLng: Map({lat: null, lng: null}),
                             objectCount: 2,
                             triggerCount: 0,
                             markers: List.of(),
                             triggers: List.of(),
                             remoteLocationsList: Map({locations:List.of(), error: null, loading: false}),
                             forceMapCenter: true,
                             mapZoom: 13,
                             selectedMarker: null,
                             editingMarker: null,
                             editingTrigger: null
                         });
    return cleanState
}

function setAdventureId(state, id) {
    return state.set('id', id)
}

function setLatLng(state, lat, lng) {
    return state.set('lat', lat).set('lng', lng);
}

function createMarker(state, latlng, title){
    const marker = Map({latlng: latlng, key: uuidv1(), title: title});
    const newState = state.set('markers', state.get('markers').push(marker));
    return newState.set('objectCount', newState.get('objectCount')+1)
}

function removeMarker(state, key){
    const markers = state.get('markers');
    const without = markers.filter(function(o) { return o.get('key') != key; });
    return state.set('markers', without);
}

function updateMarker(state, marker){
    const markers = state.get('markers');

    const newMarkers = markers.map(function(o) {
        if(o.get('key') === marker.get('key')){
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
    return state.set('mapJumpTo', Map({latlng: latlng, locationType: locationType}));
}

function mapJumped(state){
    return state.set('mapJumpTo', null);
}

function loadAdventure(state){
    return state.set('loadingAdventure', true).set('adventureError', null)
}

function loadAdventureSuccess(state, payload){
    const newState = Immutable.fromJS(payload)
    const markers = newState.get('markers').map(function(m){return m.set('key', uuidv1())})
    var markerKeyByIdMapBuilder = new Object();
    markers.map(function(m){markerKeyByIdMapBuilder[m.get('id')] = m.get('key')})
    const markerKeyByIdMap = Map(markerKeyByIdMapBuilder)
    const triggers = newState.get('triggers').map(function(m){
        const event = triggerItemVariableAssignmentsInjectKeys(m.get('event').set('key', uuidv1()), markerKeyByIdMap)
        const actions = m.get('actions', List.of()).map(function(m){return triggerItemVariableAssignmentsInjectKeys(m.set('key', uuidv1()), markerKeyByIdMap)})
        return m.set('key', uuidv1()).set('actions', actions).set('event', event)
    })
    const withKeys = newState.set('markers', markers).set('triggers', triggers)
    return state.merge(withKeys).set('loadingAdventure', false).set('adventureError', null)
}

//HELPER FUNCTIONS

function triggerItemVariableAssignmentsInjectKeys(item, markerKeyByIdMap){
    const assignments = item.get('varAssignments')
    const newAssignments = assignments.map(function(assignment){
        return assignment.set('varAssignment', assignment.get('varAssignment').map(function(a){return objectVariableAssignmentInjectKey(a, markerKeyByIdMap)}))
    });
    return item.set('varAssignments', newAssignments)
}

function objectVariableAssignmentInjectKey(variableAssignment, markerKeyByIdMap){
    if(variableAssignment.get('objectId')){
        return variableAssignment.set('objectKey', markerKeyByIdMap.get(String(variableAssignment.get('objectId'))))
    } else {
        return variableAssignment
    }
}

//END OF HELPER FUNCTIONS

function loadAdventureError(state, error){
    return state.set('loadingAdventure', false).set('adventureError', error)
}

function saveAdventure(state){
    return state.set('savingAdventure', Map({error: null, loading: true}))
}

function saveAdventureSuccess(state, payload){
    const newState = loadAdventureSuccess(state, payload)
    return newState.set('savingAdventure', Map({error: null, loading: false}))
}

function saveAdventureError(state, error){
    return state.set('savingAdventure', Map({error: error, loading: false}))
}

function loadTriggerElementSubTypes(state){
    return state.set('triggerElementSubTypeSpecification', Map({specification: Map, error: null, loading: true}));
}

function loadTriggerElementSubTypesSuccess(state, loaded){
    return state.set('triggerElementSubTypeSpecification', Map({specification: Immutable.fromJS(loaded), error: null, loading: false}));
}

function loadTriggerElementSubTypesError(state, error){
    return state.set('triggerElementSubTypeSpecification', Map({specification: Map(), error: error, loading: false}));
}

function searchRemoteLocations(state) {
    return state.set('remoteLocationsList', Map({locations: List.of(), error: null, loading: true}));
}

function searchRemoteLocationsSuccess(state, locations) {
    return state.set('remoteLocationsList', Map({locations: locations, error: null, loading: false}));
}

function searchRemoteLocationsError(state, error) {
    return state.set('remoteLocationsList', Map({locations: List.of(), error: error, loading: false}));
}

function clearRemoteLocationSearchResults(state){
    return state.set('remoteLocationsList', Map({locations: List.of(), error: null, loading: false}));
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

    const trigger = Map({key: uuidv1(), title: title, event: Map({key: uuidv1()}), actions: List.of(Map({key: uuidv1()}))});
    const newState = state.set('triggers', state.get('triggers').push(trigger));
    return newState.set('triggerCount', newState.get('triggerCount')+1)
}

function removeTrigger(state, key){
    const triggers = state.get('triggers');
    const without = triggers.filter(function(o) { return o.get('key') != key; });
    return state.set('triggers', without);
}

function updateTrigger(state, trigger){
    const triggers = state.get('triggers');

    const newTriggers = triggers.map(function(o) {
        if(o.get('key') === trigger.get('key')){
            return trigger
        } else {
            return o
        }
    });

    return state.set('triggers', newTriggers);
}

function stageTriggerForEdit(state, trigger){
    return state.set('editingTrigger', trigger);
}

function unstageTriggerForEdit(state){
    return state.set('editingTrigger', null);
}

function updateTitleOfStagedTrigger(editingTriggerState, title){
    return editingTriggerState.set('title', title)
}

function addActionToStagedTrigger(editingTriggerState){
    const newAction = Map({key: uuidv1()})

    return editingTriggerState.set('actions', editingTriggerState.get('actions').push(newAction))
}

//UTILITY FUNCTIONS

function setItemByItemType(editingTriggerState, itemType, newItem){
    if(itemType === "action"){
        const actions = editingTriggerState.get('actions')
        const newActions = actions.map(function(o){
            if(o.get('key') === newItem.get('key')){
                return newItem
            } else{
                return o
            }
        })

        return editingTriggerState.set('actions', newActions)
    } else if(itemType === "event"){
        return editingTriggerState.set('event', newItem)
    } else {
        console.log("VERY BAD")
    }
}

function getEditingItem(editingTriggerState, itemType, itemKey){
    if(itemType === "action"){
        const actions = editingTriggerState.get('actions');
        return _.find(actions.toArray(), function(o){return o.get('key') === itemKey});
    }else if(itemType === "event"){
        return editingTriggerState.get('event');
    }else {
        console.log("VERY BAD")
    }
}

//END OF UTILITY FUNCTIONS

function updateItemOfStagedTrigger(editingTriggerState, itemType, itemSubTypeId, key, triggerModel){
    var newVarAssignments = List.of()
    if(itemSubTypeId){
        const itemSubType = itemSubTypeByItemTypeAndId(itemType, itemSubTypeId, triggerModel)
        const variables = itemSubType.get('variables') ? itemSubType.get('variables') : List.of();
        newVarAssignments = List(variables.map(v => Map({varAssignment: List.of(null)})))
    }

    const newItem = Map({itemSubTypeId: itemSubTypeId, key: key, varAssignments: newVarAssignments})

    return setItemByItemType(editingTriggerState, itemType, newItem)
}

function removeActionFromStagedTrigger(editingTriggerState, key){
    const actions = editingTriggerState.get('actions');
    const without = actions.filter(function(o) { return o.get('key') != key });
    return editingTriggerState.set('actions', without);
}

function updateItemVariableAssignmentInStagedTrigger(editingTriggerState, itemType, itemKey, variableIndex, arrayIndex, assignment){
    const editingItem = getEditingItem(editingTriggerState, itemType, itemKey)

    const newEditingItem = editingItem.setIn(['varAssignments', variableIndex, 'varAssignment', arrayIndex], assignment)

    return setItemByItemType(editingTriggerState, itemType, newEditingItem)
}

function removeItemVariableAssignmentInStagedTrigger(editingTriggerState, itemType, itemKey, variableIndex, arrayIndex){
    const editingItem = getEditingItem(editingTriggerState, itemType, itemKey);

    const newEditingItem = editingItem.setIn(['varAssignments', variableIndex, 'varAssignment'],
        editingItem.getIn(['varAssignments', variableIndex, 'varAssignment']).delete(arrayIndex))

    return setItemByItemType(editingTriggerState, itemType, newEditingItem)
}

function addItemVariableAssignmentSlotInStagedTrigger(editingTriggerState, itemType, itemKey, variableIndex){
    const editingItem = getEditingItem(editingTriggerState, itemType, itemKey);

    const newEditingItem = editingItem.setIn(['varAssignments', variableIndex, 'varAssignment'],
        editingItem.getIn(['varAssignments', variableIndex, 'varAssignment']).push(null))

    return setItemByItemType(editingTriggerState, itemType, newEditingItem)
}


export default function(state = Map(), action) {
  switch (action.type) {
  case 'SET_STATE':
    return setState(state, action.state);
  case 'CLEAN_STATE':
    return cleanState();
  case 'SET_ADVENTURE_ID':
    return setAdventureId(state, action.id);
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
  case 'LOAD_ADVENTURE':
    return loadAdventure(state);
  case 'LOAD_ADVENTURE_SUCCESS':
    return loadAdventureSuccess(state, action.payload);
  case 'LOAD_ADVENTURE_ERROR':
    return loadAdventureError(state, action.error);
  case 'SAVE_ADVENTURE':
    return saveAdventure(state);
  case 'SAVE_ADVENTURE_SUCCESS':
    return saveAdventureSuccess(state, action.payload);
  case 'SAVE_ADVENTURE_ERROR':
    return saveAdventureError(state, action.error);
  case 'LOAD_TRIGGER_ELEMENT_SUB_TYPES':
    return loadTriggerElementSubTypes(state);
  case 'LOAD_TRIGGER_ELEMENT_SUB_TYPES_SUCCESS':
    return loadTriggerElementSubTypesSuccess(state, action.payload);
  case 'LOAD_TRIGGER_ELEMENT_SUB_TYPES_ERROR':
    return loadTriggerElementSubTypesError(state, action.payload);
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
    return stageTriggerForEdit(state, action.trigger);
  case 'UNSTAGE_TRIGGER_FOR_EDIT':
    return unstageTriggerForEdit(state);
  case 'UPDATE_TITLE_OF_STAGED_TRIGGER':
    return state.update('editingTrigger',
        editingTriggerState => updateTitleOfStagedTrigger(editingTriggerState, action.title));
  case 'ADD_ACTION_TO_STAGED_TRIGGER':
    return state.update('editingTrigger',
       editingTriggerState => addActionToStagedTrigger(editingTriggerState));
  case 'UPDATE_ITEM_OF_STAGED_TRIGGER':
    const triggerModel = state.getIn(['triggerElementSubTypeSpecification', 'specification']);
    return state.update('editingTrigger',
        editingTriggerState => updateItemOfStagedTrigger(editingTriggerState, action.itemType, action.itemSubTypeId, action.key, triggerModel));
  case 'REMOVE_ACTION_FROM_STAGED_TRIGGER':
    return state.update('editingTrigger',
        editingTriggerState => removeActionFromStagedTrigger(editingTriggerState, action.key));
  case 'UPDATE_ITEM_VARIABLE_ASSIGNMENT_IN_STAGED_TRIGGER':
    return state.update('editingTrigger',
        editingTriggerState => updateItemVariableAssignmentInStagedTrigger(editingTriggerState, action.itemType, action.itemKey, action.variableIndex, action.arrayIndex, action.assignment));
  case 'REMOVE_ITEM_VARIABLE_ASSIGNMENT_IN_STAGED_TRIGGER':
    return state.update('editingTrigger',
        editingTriggerState => removeItemVariableAssignmentInStagedTrigger(editingTriggerState, action.itemType, action.itemKey, action.variableIndex, action.arrayIndex));
  case 'ADD_ITEM_VARIABLE_ASSIGNMENT_SLOT_IN_STAGED_TRIGGER':
    return state.update('editingTrigger',
        editingTriggerState => addItemVariableAssignmentSlotInStagedTrigger(editingTriggerState, action.itemType, action.itemKey, action.variableIndex));
  }
  return state;
}