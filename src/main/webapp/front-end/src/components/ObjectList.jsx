import React from 'react';
import PureRenderMixin from 'react-addons-pure-render-mixin';
import {connect} from 'react-redux';
import {ListGroup, ListGroupItem, Button, ButtonToolbar} from 'react-bootstrap';
import {removeMarker, mapJumpTo, selectMarker, deselectMarker, stageMarkerForEdit} from '../action_creators';

const ObjectList = React.createClass({

    mixins: [PureRenderMixin],
    getMarkers: function() {
        return this.props.markers || [];
    },
    render: function() {
        const elementClick = this.props.removeMarker
        const mapJumpTo = this.props.mapJumpTo
        const selectMarker = this.props.selectMarker
        const deselectMarker = this.props.deselectMarker
        const stageMarkerForEdit = this.props.stageMarkerForEdit

        return  <ListGroup>
                    {this.getMarkers().map((marker) =>
                        <li href="#" className="list-group-item" key={marker.key} onMouseOut={deselectMarker} onMouseOver={() => selectMarker(marker.key)} onClick={function(){mapJumpTo(marker.latlng)}}>
                                   <h4>{marker.title}</h4>
                                   <div className="object-details">
                                        Waypoint
                                        <br />
                                        lat: {parseFloat(marker.latlng.lat.toFixed(5))}, lng: {parseFloat(marker.latlng.lng.toFixed(5))}
                                   </div>
                                   <ButtonToolbar className="pull-right" >
                                        <Button onClick={function(){stageMarkerForEdit(marker.key)}}>Edit</Button>
                                        <Button onClick={function(e){elementClick(marker.key); e.stopPropagation()}} bsStyle="danger">Delete</Button>
                                   </ButtonToolbar>
                               </li>)}
                </ListGroup>
    }

})

const mapDispatchToProps = (dispatch) => {
    return {
        removeMarker: (key) => {
            dispatch(removeMarker(key))
        },
         mapJumpTo: (latlng) => {
            dispatch(mapJumpTo(latlng, null))
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

function mapStateToProps(state) {
  return {
    markers: state.get('markers')
  };
}

export const ObjectListContainer = connect(mapStateToProps, mapDispatchToProps)(ObjectList);