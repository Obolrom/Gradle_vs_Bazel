package com.romix.feature.feat544

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat544Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat544UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat544FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat544UserSummary
)

data class Feat544UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat544NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat544Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat544Config = Feat544Config()
) {

    fun loadSnapshot(userId: Long): Feat544NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat544NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat544UserSummary {
        return Feat544UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat544FeedItem> {
        val result = java.util.ArrayList<Feat544FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat544FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat544UiMapper {

    fun mapToUi(model: List<Feat544FeedItem>): Feat544UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat544UiModel(
            header = UiText("Feat544 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat544UiModel =
        Feat544UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat544UiModel =
        Feat544UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat544UiModel =
        Feat544UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat544Service(
    private val repository: Feat544Repository,
    private val uiMapper: Feat544UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat544UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat544UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat544UserItem1(val user: CoreUser, val label: String)
data class Feat544UserItem2(val user: CoreUser, val label: String)
data class Feat544UserItem3(val user: CoreUser, val label: String)
data class Feat544UserItem4(val user: CoreUser, val label: String)
data class Feat544UserItem5(val user: CoreUser, val label: String)
data class Feat544UserItem6(val user: CoreUser, val label: String)
data class Feat544UserItem7(val user: CoreUser, val label: String)
data class Feat544UserItem8(val user: CoreUser, val label: String)
data class Feat544UserItem9(val user: CoreUser, val label: String)
data class Feat544UserItem10(val user: CoreUser, val label: String)

data class Feat544StateBlock1(val state: Feat544UiModel, val checksum: Int)
data class Feat544StateBlock2(val state: Feat544UiModel, val checksum: Int)
data class Feat544StateBlock3(val state: Feat544UiModel, val checksum: Int)
data class Feat544StateBlock4(val state: Feat544UiModel, val checksum: Int)
data class Feat544StateBlock5(val state: Feat544UiModel, val checksum: Int)
data class Feat544StateBlock6(val state: Feat544UiModel, val checksum: Int)
data class Feat544StateBlock7(val state: Feat544UiModel, val checksum: Int)
data class Feat544StateBlock8(val state: Feat544UiModel, val checksum: Int)
data class Feat544StateBlock9(val state: Feat544UiModel, val checksum: Int)
data class Feat544StateBlock10(val state: Feat544UiModel, val checksum: Int)

fun buildFeat544UserItem(user: CoreUser, index: Int): Feat544UserItem1 {
    return Feat544UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat544StateBlock(model: Feat544UiModel): Feat544StateBlock1 {
    return Feat544StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat544UserSummary> {
    val list = java.util.ArrayList<Feat544UserSummary>(users.size)
    for (user in users) {
        list += Feat544UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat544UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat544UiModel {
    val summaries = (0 until count).map {
        Feat544UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat544UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat544UiModel> {
    val models = java.util.ArrayList<Feat544UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat544AnalyticsEvent1(val name: String, val value: String)
data class Feat544AnalyticsEvent2(val name: String, val value: String)
data class Feat544AnalyticsEvent3(val name: String, val value: String)
data class Feat544AnalyticsEvent4(val name: String, val value: String)
data class Feat544AnalyticsEvent5(val name: String, val value: String)
data class Feat544AnalyticsEvent6(val name: String, val value: String)
data class Feat544AnalyticsEvent7(val name: String, val value: String)
data class Feat544AnalyticsEvent8(val name: String, val value: String)
data class Feat544AnalyticsEvent9(val name: String, val value: String)
data class Feat544AnalyticsEvent10(val name: String, val value: String)

fun logFeat544Event1(event: Feat544AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat544Event2(event: Feat544AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat544Event3(event: Feat544AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat544Event4(event: Feat544AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat544Event5(event: Feat544AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat544Event6(event: Feat544AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat544Event7(event: Feat544AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat544Event8(event: Feat544AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat544Event9(event: Feat544AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat544Event10(event: Feat544AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat544Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat544Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat544Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat544Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat544Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat544Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat544Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat544Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat544Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat544Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat544(u: CoreUser): Feat544Projection1 =
    Feat544Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat544Projection1> {
    val list = java.util.ArrayList<Feat544Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat544(u)
    }
    return list
}
