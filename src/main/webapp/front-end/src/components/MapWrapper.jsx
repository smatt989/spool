import React from 'react';
import { render } from 'react-dom';
import PureRenderMixin from 'react-addons-pure-render-mixin';
import {connect} from 'react-redux';
import {setLatLng, createMarker, stageMarkerForEdit, updateMarker, mapCentered, mapJumped, selectMarker, deselectMarker} from '../action_creators';
import L from 'leaflet';
import { List } from 'immutable';
import {markerByKey, showProps} from '../utilities';

var _ = require('lodash');

const MapWrapper = React.createClass({
  mixins: [PureRenderMixin],
  getObjectNumber: function(){
    return this.props.objectCount || 0;
  },
  getPosition: function() {
    return [40.738590, -73.987687];
  },
  getZoom: function() {
    return this.props.mapZoom || null;
  },
  getMarkers: function() {
    return this.props.markers || [];
  },
  getLat: function() {
    return this.props.lat || null
  },
  getLng: function() {
    return this.props.lng || null
  },
  getForceMapCenter() {
    return this.props.forceMapCenter || false
  },
  getIsClickOnMarker: function() {
    return this.map.clickOnMarker
  },
  getSelectedMarker: function() {
    return this.props.selectedMarker
  },
  setIsClickOnMarker: function() {
    this.map.clickOnMarker = true;
  },
  setDoneWithClickOnMarker: function() {
    this.map.clickOnMarker = false;
  },
  updateMarkerElement: function(marker) {
    var element = this.markerElementByKey(marker.key);
    element.setLatLng(marker.latlng).bindPopup('<p>'+marker.title+'</p>');
    element.title = marker.title
  },
  markerSelection: function() {
    const selectedKey = this.getSelectedMarker()
    this.markerElements.map(function(m){m.closePopup()});
    if(selectedKey != null){
        this.markerElementByKey(selectedKey).openPopup();
    }
  },
  markerElementByKey: function(key){
    const markerElements = this.markerElements
    return _.find(markerElements, function(o){return o.options.key === key})
  },
  markerElementFromMarker: function(marker){
    return new customMarker(marker.latlng,
            {key: marker.key, radius: 20, draggable: true}
            ).bindPopup('<p>'+marker.title+'</p>')
  },
  renderMarker: function(newMarker) {
    const removeMarker = this.props.removeMarker
    const setIsClickOnMarker = this.setIsClickOnMarker
    const setDoneWithClickOnMarker = this.setDoneWithClickOnMarker
    const updateMarker = this.props.updateMarker
    const selectMarker = this.props.selectMarker
    const deselectMarker = this.props.deselectMarker
    const stageMarkerForEdit = this.props.stageMarkerForEdit
    const markers = this.getMarkers

    const namedEventListener = function(e){
        var updatedMarker = markerByKey(newMarker.key, markers())
        updatedMarker.latlng = e.latlng
        updateMarker(updatedMarker);
    }

     const selectMarkerMapListener = function(){
        selectMarker(newMarker.key);
     };

    var map = this.map;

    var toInsert = this.markerElementFromMarker(newMarker);
    this.markerElements.push(toInsert)

    toInsert.addTo(map)

    toInsert.on('click', function(a){stageMarkerForEdit(newMarker.key);});

    toInsert.on({mousedown: function () {
             setIsClickOnMarker();
             deselectMarker();
             toInsert.off('mouseover', selectMarkerMapListener);
             map.dragging.disable()
             map.on('mousemove', namedEventListener);
         }
     });

     toInsert.on('mouseover', selectMarkerMapListener);

     toInsert.on('mouseout', function(){
        deselectMarker();
     });

     toInsert.on('mouseup', function() {
        setDoneWithClickOnMarker();
     })

     map.on('mouseup',function(e){
       map.dragging.enable();
       map.off('mousemove', namedEventListener);
       toInsert.on('mouseover', selectMarkerMapListener)
       //setDoneWithClickOnMarker();
     });
  },
  removeMarkerLayer: function(oldMarker) {
    var map = this.map;
    const toRemove = this.markerElementByKey(oldMarker.key)
    map.removeLayer(toRemove);
    this.markerElements = _.pullAllWith(this.markerElements, oldMarker, function(a, b){return a.key === b.key});
  },
  checkMarkerSameness: function(markerA, markerB) {
    return markerA.latlng === markerB.latlng && markerA.title === markerB.title;
  },
  centerMap: function(){
    var map = this.map;
    const markers = this.getMarkers();
    const markerLatLngs = markers.map(function(m){return m.latlng})
    map.fitBounds(markerLatLngs);
  },
  updateCenteredMap: function() {
    if(this.getForceMapCenter()){
        this.centerMap();
        this.props.markMapCentered();
    }
  },
  getMapJumpTo: function() {
    return this.props.mapJumpTo || null;
  },
  mapJumpTo: function(latlng, zoom){
    var map = this.map;
    map.setView(latlng, zoom);
  },
  zoomLevelFromLocationType: function(locationType){
    switch (locationType){
        case "address":
            return 16;
        case "venue":
            return 15;
        case "street":
            return 15;
        case "neighbourhood":
            return 13;
        case "locality":
            return 13;
        case "region":
            return 11;
        case "country":
            return 5;
        default:
            return 11;
    }
  },
  updateMapJumpTo: function(){
    const jumpTo = this.getMapJumpTo()
    if(jumpTo != null){
        const latlng = jumpTo.latlng
        var zoom = this.map.getZoom()
        if(jumpTo.locationType != null){
            zoom = this.zoomLevelFromLocationType(jumpTo.locationType);
        }
        this.mapJumpTo(latlng, zoom);
        this.props.markMapJumped();
    }
  },
  updateMarkers: function() {
        const checkMarkerSameness = this.checkMarkerSameness;
        const renderMarker = this.renderMarker;
        const removeMarker = this.removeMarkerLayer;
        const updateMarker = this.updateMarkerElement;

        const stateMarkers = this.getMarkers();
        const elementMarkers = this.markers;

        const noNeedToUpdateTest = function(a, b){
            return !(a.key !== b.key || (a.key === b.key && checkMarkerSameness(a,b)))
        }

        const toCreate = _.differenceBy(stateMarkers, elementMarkers, 'key');
        const toDelete = _.differenceBy(elementMarkers, stateMarkers, 'key');

        var toUpdate = []
        stateMarkers.map(function(sm){
            elementMarkers.map(function(em){
                if(sm.key === em.key && !checkMarkerSameness(sm, em)){
                    toUpdate.push(sm);
                }
            })
        })

        //console.log("TO UPDATE: "+toUpdate.length)
        //console.log("TO CREATE: "+toCreate.length)
        //console.log("TO DELETE: "+toDelete.length)

        toCreate.map(function(marker){
            renderMarker(marker)})

        toDelete.map(function(marker){
            removeMarker(marker)})

        toUpdate.map(function(marker){
            updateMarker(marker)})

        this.markers = stateMarkers;
  },
  initializeMap: function() {
        const setCoordinates = this.props.setCoordinates
        const addMarker = this.props.addMarker
        const setDoneWithClickOnMarker = this.setDoneWithClickOnMarker
        const getIsClickOnMarker = this.getIsClickOnMarker

        this.markers = []
        this.currentObjectNumber = 0;
        this.markerElements = []

        this.map = L.map(this.wrapper).setView(this.getPosition(), this.getZoom());
        var myMap = this.map
        var objectCounter = this.getObjectNumber;
        L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png').addTo(this.map);
        this.map.on('click', function(a){
            if(!getIsClickOnMarker()){
                addMarker(a, "Waypoint "+(objectCounter()+ 1));
            } else {
                setDoneWithClickOnMarker();
            }
        });
        this.map.on('mousemove', function(a){setCoordinates(a)});
        this.map.off('dblclick');
  },
  componentDidMount: function() {
        this.initializeMap();
        this.updateMarkers();
        this.updateCenteredMap();
  },
  componentDidUpdate: function() {
        this.updateMarkers();
        this.updateCenteredMap();
        this.updateMapJumpTo();
        this.markerSelection();
  },
  render: function() {
    return <div>
        <div id="mapid" ref={(input) => {this.wrapper = input}}></div>
        <p>lat={this.getLat()} lng={this.getLng()}</p>
        </div>

  }
});

const customMarker = L.Marker.extend({
    options: {
        key: 'a unique key'
    }
})

function mapStateToProps(state) {
  return {
        lat: state.get("lat"),
        lng: state.get("lng"),
        markers: state.get("markers"),
        objectCount: state.get("objectCount"),
        forceMapCenter: state.get("forceMapCenter"),
        mapJumpTo: state.get("mapJumpTo"),
        mapZoom: state.get("mapZoom"),
        selectedMarker: state.get("selectedMarker")
  };
}

const mapDispatchToProps = (dispatch) => {
    return {
        setCoordinates: (callback) => {
            const lat = callback.latlng.lat;
            const lng = callback.latlng.lng;
            dispatch(setLatLng(lat, lng));
        },
        addMarker: (callback, title) => {
            dispatch(createMarker(callback.latlng, title));
        },
        removeMarker: (key) => {
            dispatch(removeMarker(key))
        },
        updateMarker: (marker) => {
            dispatch(updateMarker(marker))
        },
        markMapCentered: () => {
            dispatch(mapCentered())
        },
        markMapJumped: () => {
            dispatch(mapJumped())
        },
        selectMarker: (key) => {
            dispatch(selectMarker(key))
        },
        deselectMarker: () => {
            dispatch(deselectMarker())
        },
        stageMarkerForEdit: (marker) => {
            dispatch(stageMarkerForEdit(marker))
        }
    }
}

export const MapWrapperContainer = connect(mapStateToProps, mapDispatchToProps)(MapWrapper);