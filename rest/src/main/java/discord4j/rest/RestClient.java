/*
 * This file is part of Discord4J.
 *
 * Discord4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Discord4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Discord4J.  If not, see <http://www.gnu.org/licenses/>.
 */
package discord4j.rest;

import discord4j.discordjson.json.*;
import discord4j.rest.entity.*;
import discord4j.rest.request.Router;
import discord4j.rest.request.RouterOptions;
import discord4j.rest.service.*;
import discord4j.rest.util.PaginationUtil;
import discord4j.common.util.Snowflake;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;

/**
 * An aggregation of all Discord REST API resources available. Each REST resource uses its own class and uses a
 * common {@link Router} to execute requests.
 */
public class RestClient {

    private final ApplicationService applicationService;
    private final AuditLogService auditLogService;
    private final ChannelService channelService;
    private final EmojiService emojiService;
    private final GatewayService gatewayService;
    private final GuildService guildService;
    private final InviteService inviteService;
    private final UserService userService;
    private final VoiceService voiceService;
    private final WebhookService webhookService;

    /**
     * Create a {@link RestClient} with default options, using the given token for authentication.
     *
     * @param token the bot token used for authentication
     * @return a {@link RestClient} configured with the default options
     */
    public static RestClient create(String token) {
        return RestClientBuilder.createRest(token).build();
    }

    /**
     * Obtain a {@link RestClientBuilder} able to create {@link RestClient} instances, using the given token for
     * authentication.
     *
     * @param token the bot token used for authentication
     * @return a {@link RestClientBuilder}
     */
    public static RestClientBuilder<RestClient, RouterOptions> restBuilder(String token) {
        return RestClientBuilder.createRest(token);
    }

    /**
     * Create a new {@link RestClient} using the given {@link Router} as connector to perform requests.
     *
     * @param router a connector to perform requests
     */
    protected RestClient(final Router router) {
        this.applicationService = new ApplicationService(router);
        this.auditLogService = new AuditLogService(router);
        this.channelService = new ChannelService(router);
        this.emojiService = new EmojiService(router);
        this.gatewayService = new GatewayService(router);
        this.guildService = new GuildService(router);
        this.inviteService = new InviteService(router);
        this.userService = new UserService(router);
        this.voiceService = new VoiceService(router);
        this.webhookService = new WebhookService(router);
    }

    /**
     * Requests to retrieve the channel represented by the supplied ID.
     *
     * @param channelId The ID of the channel.
     * @return A {@link RestChannel} as represented by the supplied ID.
     */
    public RestChannel getChannelById(final Snowflake channelId) {
        return RestChannel.create(this, channelId.asLong());
    }

    /**
     * Requests to retrieve the channel represented by the supplied {@link ChannelData}.
     *
     * @param data The data of the channel.
     * @return A {@link RestChannel} as represented by the supplied data.
     */
    public RestChannel restChannel(ChannelData data) {
        return RestChannel.create(this, Snowflake.asLong(data.id()));
    }

    /**
     * Requests to retrieve the guild represented by the supplied ID.
     *
     * @param guildId The ID of the guild.
     * @return A {@link RestGuild} as represented by the supplied ID.
     */
    public RestGuild getGuildById(final Snowflake guildId) {
        return RestGuild.create(this, guildId.asLong());
    }

    /**
     * Requests to retrieve the guild represented by the supplied {@link GuildData}.
     *
     * @param data The data of the guild.
     * @return A {@link RestGuild} as represented by the supplied data.
     */
    public RestGuild restGuild(GuildData data) {
        return RestGuild.create(this, Snowflake.asLong(data.id()));
    }

    /**
     * Requests to retrieve the guild emoji represented by the supplied IDs.
     *
     * @param guildId The ID of the guild.
     * @param emojiId The ID of the emoji.
     * @return A {@link RestEmoji} as represented by the supplied IDs.
     */
    public RestEmoji getGuildEmojiById(final Snowflake guildId, final Snowflake emojiId) {
        return RestEmoji.create(this, guildId.asLong(), emojiId.asLong());
    }

    /**
     * Requests to retrieve the guild emoji represented by the supplied ID and {@link EmojiData}.
     *
     * @param guildId The ID of the guild.
     * @param data The data of the emoji.
     * @return A {@link RestEmoji} as represented by the supplied parameters.
     */
    public RestEmoji restGuildEmoji(Snowflake guildId, EmojiData data) {
        return RestEmoji.create(this, guildId.asLong(),
                Snowflake.asLong(data.id().orElseThrow(() -> new IllegalArgumentException("Not a guild emoji"))));
    }

    /**
     * Requests to retrieve the member represented by the supplied IDs.
     *
     * @param guildId The ID of the guild.
     * @param userId The ID of the user.
     * @return A {@link RestMember} as represented by the supplied IDs.
     */
    public RestMember getMemberById(final Snowflake guildId, final Snowflake userId) {
        return RestMember.create(this, guildId.asLong(), userId.asLong());
    }

    /**
     * Requests to retrieve the member represented by the supplied ID and {@link MemberData}
     *
     * @param guildId The ID of the guild.
     * @param data The data of the user.
     * @return A {@link RestMember} as represented by the supplied parameters.
     */
    public RestMember restMember(Snowflake guildId, MemberData data) {
        return RestMember.create(this, guildId.asLong(), Snowflake.asLong(data.user().id()));
    }

    /**
     * Requests to retrieve the message represented by the supplied IDs.
     *
     * @param channelId The ID of the channel.
     * @param messageId The ID of the message.
     * @return A {@link RestMessage} as represented by the supplied IDs.
     */
    public RestMessage getMessageById(final Snowflake channelId, final Snowflake messageId) {
        return RestMessage.create(this, channelId.asLong(), messageId.asLong());
    }

    /**
     * Requests to retrieve the message represented by the supplied {@link MessageData}.
     *
     * @param data The data of the channel.
     * @return A {@link RestMessage} as represented by the supplied data.
     */
    public RestMessage restMessage(MessageData data) {
        return RestMessage.create(this, Snowflake.asLong(data.channelId()),
                Snowflake.asLong(data.id()));
    }

    /**
     * Requests to retrieve the role represented by the supplied IDs.
     *
     * @param guildId The ID of the guild.
     * @param roleId The ID of the role.
     * @return A {@link RestRole} as represented by the supplied IDs.
     */
    public RestRole getRoleById(final Snowflake guildId, final Snowflake roleId) {
        return RestRole.create(this, guildId.asLong(), roleId.asLong());
    }

    /**
     * Requests to retrieve the role represented by the supplied ID and {@link RoleData}.
     *
     * @param guildId The ID of the guild.
     * @param data The data of the role.
     * @return A {@link RestRole} as represented by the supplied parameters.
     */
    public RestRole restRole(Snowflake guildId, RoleData data) {
        return RestRole.create(this, guildId.asLong(), Snowflake.asLong(data.id()));
    }

    /**
     * Requests to retrieve the user represented by the supplied ID.
     *
     * @param userId The ID of the user.
     * @return A {@link RestUser} as represented by the supplied ID.
     */
    public RestUser getUserById(final Snowflake userId) {
        return RestUser.create(this, userId.asLong());
    }

    /**
     * Requests to retrieve the user represented by the supplied {@link UserData}.
     *
     * @param data The data of the user.
     * @return A {@link RestUser} as represented by the supplied data.
     */
    public RestUser restUser(UserData data) {
        return RestUser.create(this, Snowflake.asLong(data.id()));
    }

    /**
     * Requests to retrieve the webhook represented by the supplied ID.
     *
     * @param webhookId The ID of the webhook.
     * @return A {@link RestWebhook} as represented by the supplied ID.
     */
    public RestWebhook getWebhookById(final Snowflake webhookId) {
        return RestWebhook.create(this, webhookId.asLong());
    }

    /**
     * Requests to retrieve the webhook represented by the supplied {@link WebhookData}.
     *
     * @param data The data of the webhook.
     * @return A {@link RestWebhook} as represented by the supplied ID.
     */
    public RestWebhook restWebhook(WebhookData data) {
        return RestWebhook.create(this, Snowflake.asLong(data.id()));
    }

    /**
     * Requests to retrieve the application info.
     *
     * @return A {@link Mono} where, upon successful completion, emits the {@link ApplicationInfoData}. If
     * an error is received, it is emitted through the {@code Mono}.
     */
    public Mono<ApplicationInfoData> getApplicationInfo() {
        return this.getApplicationService()
                .getCurrentApplicationInfo();
    }

    /**
     * Requests to retrieve the guilds the current client is in.
     *
     * @return A {@link Flux} that continually emits the {@link PartialGuildData guilds} that the current client is
     * in. If an error is received, it is emitted through the {@code Flux}.
     */
    public Flux<UserGuildData> getGuilds() {
        final Function<Map<String, Object>, Flux<UserGuildData>> makeRequest = params ->
                this.getUserService()
                        .getCurrentUserGuilds(params);

        return PaginationUtil.paginateAfter(makeRequest, data -> Snowflake.asLong(data.id()), 0L, 100);
    }

    /**
     * Requests to retrieve the voice regions that are available.
     *
     * @return A {@link Flux} that continually emits the {@link RegionData regions} that are available. If an error is
     * received, it is emitted through the {@code Flux}.
     */
    public Flux<RegionData> getRegions() {
        return this.getVoiceService().getVoiceRegions();
    }

    /**
     * Requests to retrieve the bot user.
     *
     * @return A {@link Mono} where, upon successful completion, emits the bot {@link UserData user}. If an error is
     * received, it is emitted through the {@code Mono}.
     */
    public Mono<UserData> getSelf() {
        return userService.getCurrentUser();
    }

    /**
     * Requests to create a guild.
     *
     * @param request A {@link GuildCreateRequest} as request body.
     * @return A {@link Mono} where, upon successful completion, emits the created {@link GuildUpdateData}. If an
     * error is received, it is emitted through the {@code Mono}.
     */
    public Mono<GuildUpdateData> createGuild(GuildCreateRequest request) {
        return guildService.createGuild(request);
    }

    /**
     * Requests to retrieve an invite.
     *
     * @param inviteCode The code for the invite (e.g. "xdYkpp").
     * @return A {@link Mono} where, upon successful completion, emits the {@link InviteData} as represented by the
     * supplied invite code. If an error is received, it is emitted through the {@code Mono}.
     */
    public Mono<InviteData> getInvite(final String inviteCode) {
        return inviteService.getInvite(inviteCode);
    }

    /**
     * Requests to edit this client (i.e., modify the current bot user).
     *
     * @param request A {@link UserModifyRequest} as request body.
     * @return A {@link Mono} where, upon successful completion, emits the edited {@link UserData}. If an error is
     * received, it is emitted through the {@code Mono}.
     */
    public Mono<UserData> edit(UserModifyRequest request) {
        return userService.modifyCurrentUser(request);
    }

    /**
     * Access a low-level representation of the API endpoints for the Application resource.
     *
     * @return a handle to perform low-level requests to the API
     */
    public ApplicationService getApplicationService() {
        return applicationService;
    }

    /**
     * Access a low-level representation of the API endpoints for the Audit Log resource.
     *
     * @return a handle to perform low-level requests to the API
     */
    public AuditLogService getAuditLogService() {
        return auditLogService;
    }

    /**
     * Access a low-level representation of the API endpoints for the Channel resource. It is recommended you use
     * methods like {@link #getChannelById(Snowflake)}, {@link #restChannel(ChannelData)} or
     * {@link RestChannel#create(RestClient, Snowflake)}.
     *
     * @return a handle to perform low-level requests to the API
     */
    public ChannelService getChannelService() {
        return channelService;
    }

    /**
     * Access a low-level representation of the API endpoints for the Guild Emoji resource. It is recommended you use
     * methods like {@link #getGuildEmojiById(Snowflake, Snowflake)}, {@link #restGuildEmoji(Snowflake, EmojiData)} or
     * {@link RestEmoji#create(RestClient, Snowflake, Snowflake)}.
     *
     * @return a handle to perform low-level requests to the API
     */
    public EmojiService getEmojiService() {
        return emojiService;
    }

    /**
     * Access a low-level representation of the API endpoints for the Gateway resource.
     *
     * @return a handle to perform low-level requests to the API
     */
    public GatewayService getGatewayService() {
        return gatewayService;
    }

    /**
     * Access a low-level representation of the API endpoints for the Guild resource. It is recommended you use
     * methods like {@link #getGuildById(Snowflake)}, {@link #restGuild(GuildData)} or
     * {@link RestGuild#create(RestClient, Snowflake)}.
     *
     * @return a handle to perform low-level requests to the API
     */
    public GuildService getGuildService() {
        return guildService;
    }

    /**
     * Access a low-level representation of the API endpoints for the Invite resource. It is recommended you use
     * methods like {@link #getInvite(String)}, or {@link RestInvite#create(RestClient, String)}.
     *
     * @return a handle to perform low-level requests to the API
     */
    public InviteService getInviteService() {
        return inviteService;
    }

    /**
     * Access a low-level representation of the API endpoints for the User resource. It is recommended you use
     * methods like {@link #getUserById(Snowflake)}, {@link #restUser(UserData)} or
     * {@link RestUser#create(RestClient, Snowflake)}.
     *
     * @return a handle to perform low-level requests to the API
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * Access a low-level representation of the API endpoints for the Voice Region resource.
     *
     * @return a handle to perform low-level requests to the API
     */
    public VoiceService getVoiceService() {
        return voiceService;
    }

    /**
     * Access a low-level representation of the API endpoints for the Webhook resource. It is recommended you use
     * methods like {@link #getWebhookById(Snowflake)}, {@link #restWebhook(WebhookData)} or
     * {@link RestWebhook#create(RestClient, Snowflake)}.
     *
     * @return a handle to perform low-level requests to the API
     */
    public WebhookService getWebhookService() {
        return webhookService;
    }
}
