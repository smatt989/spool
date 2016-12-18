import React from 'react';
import { render } from 'react-dom';
import PureRenderMixin from 'react-addons-pure-render-mixin';
import {connect} from 'react-redux';
import {Modal, Button, FormControl, Panel, Accordion, ButtonGroup, DropdownButton, MenuItem} from 'react-bootstrap';
import {triggerByKey} from '../utilities';
import {updateTrigger, unstageTriggerForEdit, removeTrigger} from '../action_creators';
import {triggerModel} from '../triggerModel';
import {TriggerElement} from './TriggerElement';

var _ = require('lodash');

const TriggerEditor = React.createClass({
  mixins: [PureRenderMixin],
  getTriggers: function() {
    return this.props.triggers || []
  },
  getEditingTriggerKey: function() {
    return this.props.editingTrigger || null;
  },
  editingTriggerByKey: function() {
    const key = this.getEditingTriggerKey()
    if(key != null){
      const triggers = this.getTriggers();
      return triggerByKey(key, triggers);
    }
  },
  componentWillMount: function() {
    const trigger = _.cloneDeep(this.editingTriggerByKey())
    this.updateLocalTriggerState(trigger)
  },
  updateLocalTriggerState: function(trigger){
    this.setState({trigger: trigger});
  },
  render: function() {
    const unstageTriggerForEdit = this.props.unstageTriggerForEdit
    const show = this.getEditingTriggerKey() != null
    const updateTrigger = this.props.updateTrigger
    const removeTrigger = this.props.removeTrigger
    const actions = triggerModel.actions
    const conditions = triggerModel.conditions
    const events = triggerModel.events

    const triggerEvent = _.get(this, 'state.trigger.event', null)
    const triggerActions = _.get(this, 'state.trigger.actions', [])

    const updateLocalTriggerState = this.updateLocalTriggerState

    if(!show){
        return <Modal show={show} onHide={close} />
    }
    else {
        var trigger = _.clone(this.editingTriggerByKey())

        var triggerEventTitle = "Choose event"

        if(this.state != null && this.state.trigger != null && this.state.trigger.event != null){
            triggerEventTitle = this.state.trigger.event.title
        }

        const save = function(m) {
            updateTrigger(m);
            unstageTriggerForEdit();
        }
        const remove = function() {
            removeTrigger(trigger.key);
            unstageTriggerForEdit();
        }

        return <Modal show={show} onHide={unstageTriggerForEdit}>
                 <Modal.Header closeButton>
                   <Modal.Title>{trigger.title}</Modal.Title>
                 </Modal.Header>
                 <Modal.Body>
                   <h4>Object Type: Location</h4>
                   <h4>Title: </h4><FormControl onChange={function(a){trigger.title = a.target.value}} type="text" placeholder="Title" defaultValue={trigger.title} />

                    <div>

                        <Panel header="Event">
                            <TriggerElement currentTrigger={triggerEvent} arity="one" options={events} elementType="event" />
                        </Panel>
                        <Panel header="Actions" eventKey={3}>
                            <TriggerElement currentTrigger={triggerActions} arity="many" options={actions} elementType="action" />
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
    triggers: state.get("triggers"),
    editingTrigger: state.get("editingTrigger")
  };
}

const mapDispatchToProps = (dispatch) => {
    return {
        updateTrigger: (marker) => {
            dispatch(updateTrigger(marker))
        },
        unstageTriggerForEdit: () => {
            dispatch(unstageTriggerForEdit())
        },
        removeTrigger: (key) => {
            dispatch(removeTrigger(key))
        }
    }
}

export const TriggerEditorContainer = connect(mapStateToProps, mapDispatchToProps)(TriggerEditor);