import React from 'react';
import PureRenderMixin from 'react-addons-pure-render-mixin';
import {connect} from 'react-redux';
import {ListGroup, ListGroupItem, Button, ButtonToolbar, ButtonGroup, Panel} from 'react-bootstrap';
import {removeTrigger, stageTriggerForEdit, createTrigger} from '../action_creators';
import {validateTrigger} from '../validation';

const TriggerList = React.createClass({

    mixins: [PureRenderMixin],
    getTriggers: function() {
        return this.props.triggers || [];
    },
    getTriggerCount: function() {
        return this.props.triggerCount || 0;
    },
    getMarkers: function() {
        return this.props.markers || List.of()
    },
    render: function() {
        const elementClick = this.props.removeTrigger
        const stageTriggerForEdit = this.props.stageTriggerForEdit
        const getTriggerCount = this.getTriggerCount
        const markers = this.getMarkers()

        const getValidationState = function(trigger){
            if(validateTrigger(trigger, markers)){
                return 'info'
            } else {
                return 'danger'
            }
        }


        return  <div>
                    <ButtonGroup vertical block>
                        <Button onClick={() => this.props.createTrigger("Trigger "+(getTriggerCount() + 1))} bsStyle="primary">New Trigger</Button>
                    </ButtonGroup>
                    <ListGroup>
                        {this.getTriggers().map((trigger) =>
                            <li href="#" className="list-group-item" key={trigger.get('key')}>
                                <Panel bsStyle={getValidationState(trigger)} header={trigger.get('title')}>
                                   <div className="object-details">
                                        Trigger
                                   </div>
                                   <ButtonToolbar className="pull-right" >
                                        <Button onClick={function(){stageTriggerForEdit(trigger)}}>Edit</Button>
                                        <Button onClick={function(e){elementClick(trigger.get('key')); e.stopPropagation()}} bsStyle="danger">Delete</Button>
                                   </ButtonToolbar>
                                 </Panel>
                               </li>)}
                    </ListGroup>
                </div>
    }

})

const mapDispatchToProps = (dispatch) => {
    return {
        removeTrigger: (key) => {
            dispatch(removeTrigger(key))
        },
         stageTriggerForEdit: (trigger) => {
            dispatch(stageTriggerForEdit(trigger))
         },
         createTrigger: (title) => {
            dispatch(createTrigger(title))
         }
    }
}

function mapStateToProps(state) {
  return {
    triggers: state.get('triggers'),
    triggerCount: state.get('triggerCount'),
    markers: state.get('markers')
  };
}

export const TriggerListContainer = connect(mapStateToProps, mapDispatchToProps)(TriggerList);