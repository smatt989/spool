import React from 'react';
import { render } from 'react-dom';
import PureRenderMixin from 'react-addons-pure-render-mixin';
import {connect} from 'react-redux';
import {Modal, Button, FormControl} from 'react-bootstrap';
import {updateMarker, unstageMarkerForEdit, removeMarker} from '../action_creators';
import {markerByKey} from '../utilities';

var _ = require('lodash');

const MarkerInfo = React.createClass({
  mixins: [PureRenderMixin],
  getMarkers: function() {
    return this.props.markers || []
  },
  getEditingMarkerKey: function() {
    return this.props.editingMarker || null;
  },
  editingMarkerByKey: function() {
    const key = this.getEditingMarkerKey()
    if(key != null){
      const markers = this.getMarkers()
      return markerByKey(key, markers)
    }
  },
  render: function() {
    const unstageMarkerForEdit = this.props.unstageMarkerForEdit
    const show = this.getEditingMarkerKey() != null
    const updateMarker = this.props.updateMarker
    const removeMarker = this.props.removeMarker

    if(!show){
        return <Modal show={show} onHide={close} />
    }
    else {
        var marker = this.editingMarkerByKey()
        const save = function(m) {
            updateMarker(m);
            unstageMarkerForEdit();
        }
        const remove = function() {
            removeMarker(marker.get('key'));
            unstageMarkerForEdit();
        }

        return <Modal show={show} onHide={unstageMarkerForEdit}>
                 <Modal.Header closeButton>
                   <Modal.Title>{marker.get('title')}</Modal.Title>
                 </Modal.Header>
                 <Modal.Body>
                   <h4>Object Type: Location</h4>
                   <h4>Title: </h4><FormControl onChange={function(a){marker = marker.set('title', a.target.value)}} type="text" placeholder="Title" defaultValue={marker.get('title')} />
                 </Modal.Body>
                 <Modal.Footer>
                   <Button onClick={unstageMarkerForEdit}>Close</Button>
                   <Button onClick={remove} bsStyle="danger">Delete</Button>
                   <Button onClick={() => save(marker)} bsStyle="primary">Save changes</Button>
                 </Modal.Footer>
               </Modal>
        }
  }
});

function mapStateToProps(state) {
  return {
    editingMarker: state.get('editingMarker'),
    markers: state.get('markers')
  };
}

const mapDispatchToProps = (dispatch) => {
    return {
        updateMarker: (marker) => {
            dispatch(updateMarker(marker))
        },
        unstageMarkerForEdit: () => {
            dispatch(unstageMarkerForEdit())
        },
        removeMarker: (key) => {
            dispatch(removeMarker(key))
        }
    }
}

export const MarkerInfoContainer = connect(mapStateToProps, mapDispatchToProps)(MarkerInfo);