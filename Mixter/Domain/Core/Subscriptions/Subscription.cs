﻿using System.Collections.Generic;
using Mixter.Domain.Core.Messages;
using Mixter.Domain.Core.Subscriptions.Events;

namespace Mixter.Domain.Core.Subscriptions
{
    public class Subscription
    {
        private readonly DecisionProjection _projection;

        public Subscription(IEnumerable<IDomainEvent> events)
        {
            _projection = new DecisionProjection();

            foreach (var evt in events)
            {
                _projection.Apply(evt);
            }
        }

        public SubscriptionId GetId()
        {
            return _projection.Id;
        }

        public static void FollowUser(IEventPublisher eventPublisher, UserId follower, UserId followee)
        {
            var userFollowed = new UserFollowed(new SubscriptionId(follower, followee));
            eventPublisher.Publish(userFollowed);
        }

        public void Unfollow(IEventPublisher eventPublisher)
        {
            eventPublisher.Publish(new UserUnfollowed(_projection.Id));
        }

        public void NotifyFollower(IEventPublisher eventPublisher, MessageId messageId)
        {
            if (_projection.IsUnfollow)
            {
                return;
            }

            eventPublisher.Publish(new FollowerMessagePublished(_projection.Id, messageId));
        }

        private class DecisionProjection : DecisionProjectionBase
        {
            public DecisionProjection()
            {
                AddHandler<UserUnfollowed>(When);
                AddHandler<UserFollowed>(When);
            }

            public SubscriptionId Id { get; private set; }

            public bool IsUnfollow { get; private set; }

            private void When(UserFollowed evt)
            {
                Id = evt.SubscriptionId;
            }

            private void When(UserUnfollowed evt)
            {
                IsUnfollow = true;
            }
        }
    }
}