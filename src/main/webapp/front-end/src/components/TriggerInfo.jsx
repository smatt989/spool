import React from 'react';
import { render } from 'react-dom';
import PureRenderMixin from 'react-addons-pure-render-mixin';
import {connect} from 'react-redux';
import {Modal, Button, FormControl, Panel, ListGroup} from 'react-bootstrap';
import {updateTrigger, unstageTriggerForEdit, removeTrigger, addActionToStagedTrigger, updateTitleOfStagedTrigger} from '../action_creators';
import {triggerByKey} from '../utilities';
import {triggerModel} from '../triggerModel';
import {TriggerItemContainer} from './TriggerItem';
import {validateTrigger} from '../validation';

var _ = require('lodash');

const TriggerInfo = React.createClass({
  mixins: [PureRenderMixin],
  getEditingTrigger: function() {
    return this.props.editingTrigger || null;
  },
  render: function() {
    const unstageTriggerForEdit = this.props.unstageTriggerForEdit
    const show = this.getEditingTrigger() != null
    const updateTrigger = this.props.updateTrigger
    const removeTrigger = this.props.removeTrigger
    const addAction = this.props.addActionToStagedTrigger
    const updateTitle = this.props.updateTitleOfStagedTrigger
    const markers = this.props.markers

    if(!show){
        return <Modal show={show} onHide={close} />
    }
    else {
        const trigger = this.getEditingTrigger()
        const save = function(m) {
            updateTrigger(m);
            unstageTriggerForEdit();
        }
        const remove = function() {
            removeTrigger(trigger.get('key'));
            unstageTriggerForEdit();
        }

        const event = trigger.get('event')

        const danger = validateTrigger(trigger, markers) ? <p>THIS IS GOOD</p> : <p>THIS IS BAD</p>

        return <Modal show={show} onHide={unstageTriggerForEdit}>
                 <Modal.Header closeButton>
                   <Modal.Title>{trigger.get('title')}</Modal.Title>
                 </Modal.Header>
                 <Modal.Body>
                   <h4>Object Type: Location</h4>
                   <h4>Title: </h4><FormControl onChange={function(a){updateTitle(a.target.value)}} type="text" placeholder="Title" defaultValue={trigger.get('title')} />
                   {danger}
                     <div>

                         <Panel header="Event">
                            <TriggerItemContainer key={event.get('key')} item={event} arity="one" options={triggerModel.events} elementType="event" />
                         </Panel>
                         <Panel header="Actions" eventKey={3}>
                            <ListGroup>
                                 {trigger.get('actions').map(function(a){
                                     return <li key={a.get('key')} className="list-group-item"><TriggerItemContainer key={a.get('key')} item={a} arity="many" options={triggerModel.actions} elementType="action" /></li>
                                 })}
                             </ListGroup>
                             <Button bsStyle="primary" onClick={() => addAction()}>Add an Action</Button>
                         </Panel>

                     </div>

                 </Modal.Body>
                 <Modal.Footer>
                   <Button onClick={unstageTriggerForEdit}>Close</Button>
                   <Button onClick={remove} bsStyle="danger">Delete</Button>
                   <Button onClick={() => save(trigger)} bsStyle="primary">Save changes</Button>
                 </Modal.Footer>
               </Modal>
        }
  }
});

function mapStateToProps(state) {
  return {
    editingTrigger: state.get('editingTrigger'),
    triggers: state.get('triggers'),
    markers: state.get('markers')
  };
}

const mapDispatchToProps = (dispatch) => {
    return {
        updateTrigger: (trigger) => {
            dispatch(updateTrigger(trigger))
        },
        unstageTriggerForEdit: () => {
            dispatch(unstageTriggerForEdit())
        },
        removeTrigger: (key) => {
            dispatch(removeTrigger(key))
        },
        addActionToStagedTrigger: () => {
            dispatch(addActionToStagedTrigger())
        },
        updateTitleOfStagedTrigger: (title) => {
            dispatch(updateTitleOfStagedTrigger(title))
        }
    }
}

export const TriggerInfoContainer = connect(mapStateToProps, mapDispatchToProps)(TriggerInfo);