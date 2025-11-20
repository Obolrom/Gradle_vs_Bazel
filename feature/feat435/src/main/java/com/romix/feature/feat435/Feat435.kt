package com.romix.feature.feat435

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat435Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat435UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat435FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat435UserSummary
)

data class Feat435UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat435NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat435Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat435Config = Feat435Config()
) {

    fun loadSnapshot(userId: Long): Feat435NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat435NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat435UserSummary {
        return Feat435UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat435FeedItem> {
        val result = java.util.ArrayList<Feat435FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat435FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat435UiMapper {

    fun mapToUi(model: List<Feat435FeedItem>): Feat435UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat435UiModel(
            header = UiText("Feat435 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat435UiModel =
        Feat435UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat435UiModel =
        Feat435UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat435UiModel =
        Feat435UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat435Service(
    private val repository: Feat435Repository,
    private val uiMapper: Feat435UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat435UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat435UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat435UserItem1(val user: CoreUser, val label: String)
data class Feat435UserItem2(val user: CoreUser, val label: String)
data class Feat435UserItem3(val user: CoreUser, val label: String)
data class Feat435UserItem4(val user: CoreUser, val label: String)
data class Feat435UserItem5(val user: CoreUser, val label: String)
data class Feat435UserItem6(val user: CoreUser, val label: String)
data class Feat435UserItem7(val user: CoreUser, val label: String)
data class Feat435UserItem8(val user: CoreUser, val label: String)
data class Feat435UserItem9(val user: CoreUser, val label: String)
data class Feat435UserItem10(val user: CoreUser, val label: String)

data class Feat435StateBlock1(val state: Feat435UiModel, val checksum: Int)
data class Feat435StateBlock2(val state: Feat435UiModel, val checksum: Int)
data class Feat435StateBlock3(val state: Feat435UiModel, val checksum: Int)
data class Feat435StateBlock4(val state: Feat435UiModel, val checksum: Int)
data class Feat435StateBlock5(val state: Feat435UiModel, val checksum: Int)
data class Feat435StateBlock6(val state: Feat435UiModel, val checksum: Int)
data class Feat435StateBlock7(val state: Feat435UiModel, val checksum: Int)
data class Feat435StateBlock8(val state: Feat435UiModel, val checksum: Int)
data class Feat435StateBlock9(val state: Feat435UiModel, val checksum: Int)
data class Feat435StateBlock10(val state: Feat435UiModel, val checksum: Int)

fun buildFeat435UserItem(user: CoreUser, index: Int): Feat435UserItem1 {
    return Feat435UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat435StateBlock(model: Feat435UiModel): Feat435StateBlock1 {
    return Feat435StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat435UserSummary> {
    val list = java.util.ArrayList<Feat435UserSummary>(users.size)
    for (user in users) {
        list += Feat435UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat435UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat435UiModel {
    val summaries = (0 until count).map {
        Feat435UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat435UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat435UiModel> {
    val models = java.util.ArrayList<Feat435UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat435AnalyticsEvent1(val name: String, val value: String)
data class Feat435AnalyticsEvent2(val name: String, val value: String)
data class Feat435AnalyticsEvent3(val name: String, val value: String)
data class Feat435AnalyticsEvent4(val name: String, val value: String)
data class Feat435AnalyticsEvent5(val name: String, val value: String)
data class Feat435AnalyticsEvent6(val name: String, val value: String)
data class Feat435AnalyticsEvent7(val name: String, val value: String)
data class Feat435AnalyticsEvent8(val name: String, val value: String)
data class Feat435AnalyticsEvent9(val name: String, val value: String)
data class Feat435AnalyticsEvent10(val name: String, val value: String)

fun logFeat435Event1(event: Feat435AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat435Event2(event: Feat435AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat435Event3(event: Feat435AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat435Event4(event: Feat435AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat435Event5(event: Feat435AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat435Event6(event: Feat435AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat435Event7(event: Feat435AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat435Event8(event: Feat435AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat435Event9(event: Feat435AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat435Event10(event: Feat435AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat435Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat435Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat435Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat435Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat435Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat435Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat435Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat435Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat435Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat435Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat435(u: CoreUser): Feat435Projection1 =
    Feat435Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat435Projection1> {
    val list = java.util.ArrayList<Feat435Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat435(u)
    }
    return list
}
