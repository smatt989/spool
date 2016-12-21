import React from 'react';
import PureRenderMixin from 'react-addons-pure-render-mixin';
import {connect} from 'react-redux';
import {Map, List} from 'immutable';
import {validateTrigger} from '../validation';
import {Button, ButtonToolbar, FormGroup, FormControl, Navbar, ListGroup, ListGroupItem} from 'react-bootstrap';
import {forceMapCenter, mapJumpTo, clearRemoteLocationSearchResults, searchRemoteLocations, searchRemoteLocationsSuccess, searchRemoteLocationsError, saveAdventure, saveAdventureSuccess, saveAdventureError, cleanState} from '../action_creators'

const hashHistory = require('react-router').hashHistory;

const MapToolbar = React.createClass({
    mixins: [PureRenderMixin],
    getSearchResults: function() {
        return this.props.remoteLocationsList.get('locations') || List.of()
    },
    searchWithQuery: function(event){
        if(event.target.value.length > 2){
            this.props.searchRemoteLocations(event.target.value);
        }
    },
    selectSearchResult: function(latlng, locationType){
        this.props.mapJumpTo(latlng, locationType);
        this.props.clearRemoteLocationSearchResults();
    },
    render: function(){
        const selectSearchResult = this.selectSearchResult
        const cleanState = this.props.cleanState;


        const newAdventure = function(){
            cleanState();
            hashHistory.push("/");
        }

        const markers = this.props.markers;
        const triggers = this.props.triggers;

        var triggerModel;
        if(this.props.triggerModel){
            triggerModel = this.props.triggerModel.get('specification')
        } else{
            triggerModel = Map()
        }

        const getValidationState = function(trigger){
            if(triggerModel.size > 0 && validateTrigger(trigger, markers, triggerModel)){
                return 'info'
            } else {
                return 'danger'
            }
        }

        const saveAdventure = () => {
            if(triggers.every(function(trigger){return validateTrigger(trigger, markers, triggerModel)})){
                this.props.saveAdventure(this.props.id, this.props.name, this.props.description, triggers, markers)
            } else {
                alert("please check that all your triggers are working")
            }
        }

        return   <ButtonToolbar>
                   <Button onClick={this.props.centerMap}>Center Map</Button>
                   <Navbar.Form pullLeft>
                    <FormGroup>
                      <FormControl id="search-input" ref={(input) => {this.searchInput = input}} onKeyUp={this.searchWithQuery} type="text" placeholder="Search" />
                      <ListGroup className="map-search-results">
                        {this.getSearchResults().map(result =>
                            <ListGroupItem onClick={() => selectSearchResult(Map({lat: result.geometry.coordinates[1], lng: result.geometry.coordinates[0]}), result.properties.layer)} key={result.properties.id} href="#">
                                <h6>{result.properties.label}</h6>
                                <i className="small-details">{result.properties.layer}</i>
                            </ListGroupItem>
                        )}
                      </ListGroup>
                    </FormGroup>
                    </Navbar.Form>
                    <Button onClick={saveAdventure}>Save Adventure</Button>
                    <Button onClick={newAdventure}>New Adventure</Button>
                 </ButtonToolbar>;
    }
});

function mapStateToProps(state) {
  return {
    remoteLocationsList: state.get('remoteLocationsList'),
    id: state.get('id', 0),
    name: state.get('name', null),
    description: state.get('description', null),
    triggers: state.get('triggers', List.of()),
    markers: state.get('markers', List.of()),
    triggerModel: state.get('triggerElementSubTypeSpecification')
  };
}

const mapDispatchToProps = (dispatch) => {
    return {
        centerMap: () => {
            dispatch(forceMapCenter())
        },
        searchRemoteLocations: (query) => {
            dispatch(searchRemoteLocations(query)).then((response) => {
               !response.error ? dispatch(searchRemoteLocationsSuccess(response.payload.data.features)) : dispatch(searchRemoteLocationsError(response.payload));
           });
        },
        mapJumpTo: (latlng, locationType) => {
            dispatch(mapJumpTo(latlng, locationType))
        },
        clearRemoteLocationSearchResults: () => {
            dispatch(clearRemoteLocationSearchResults())
        },
        saveAdventure: (id, name, description, triggers, markers) => {
            const adventureObject = Map({id: id, name: name, description: description, triggers: triggers, markers: markers});
            dispatch(saveAdventure(adventureObject)).then((response) => {
                if(!response.error){
                    dispatch(saveAdventureSuccess(response.payload.data));
                    hashHistory.push('/adventures/'+response.payload.data.id+'/edit')
                } else {
                    dispatch(saveAdventureError(response.error));
                }
            });
        },
        cleanState: () => dispatch(cleanState())

    }
}

export const MapToolbarContainer = connect(mapStateToProps, mapDispatchToProps)(MapToolbar);

function showProps(obj){
    for(var key in obj){
        console.log(key+": "+obj[key]);
    }
}