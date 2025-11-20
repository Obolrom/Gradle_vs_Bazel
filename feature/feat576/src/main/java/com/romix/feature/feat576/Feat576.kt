package com.romix.feature.feat576

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat576Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat576UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat576FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat576UserSummary
)

data class Feat576UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat576NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat576Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat576Config = Feat576Config()
) {

    fun loadSnapshot(userId: Long): Feat576NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat576NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat576UserSummary {
        return Feat576UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat576FeedItem> {
        val result = java.util.ArrayList<Feat576FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat576FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat576UiMapper {

    fun mapToUi(model: List<Feat576FeedItem>): Feat576UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat576UiModel(
            header = UiText("Feat576 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat576UiModel =
        Feat576UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat576UiModel =
        Feat576UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat576UiModel =
        Feat576UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat576Service(
    private val repository: Feat576Repository,
    private val uiMapper: Feat576UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat576UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat576UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat576UserItem1(val user: CoreUser, val label: String)
data class Feat576UserItem2(val user: CoreUser, val label: String)
data class Feat576UserItem3(val user: CoreUser, val label: String)
data class Feat576UserItem4(val user: CoreUser, val label: String)
data class Feat576UserItem5(val user: CoreUser, val label: String)
data class Feat576UserItem6(val user: CoreUser, val label: String)
data class Feat576UserItem7(val user: CoreUser, val label: String)
data class Feat576UserItem8(val user: CoreUser, val label: String)
data class Feat576UserItem9(val user: CoreUser, val label: String)
data class Feat576UserItem10(val user: CoreUser, val label: String)

data class Feat576StateBlock1(val state: Feat576UiModel, val checksum: Int)
data class Feat576StateBlock2(val state: Feat576UiModel, val checksum: Int)
data class Feat576StateBlock3(val state: Feat576UiModel, val checksum: Int)
data class Feat576StateBlock4(val state: Feat576UiModel, val checksum: Int)
data class Feat576StateBlock5(val state: Feat576UiModel, val checksum: Int)
data class Feat576StateBlock6(val state: Feat576UiModel, val checksum: Int)
data class Feat576StateBlock7(val state: Feat576UiModel, val checksum: Int)
data class Feat576StateBlock8(val state: Feat576UiModel, val checksum: Int)
data class Feat576StateBlock9(val state: Feat576UiModel, val checksum: Int)
data class Feat576StateBlock10(val state: Feat576UiModel, val checksum: Int)

fun buildFeat576UserItem(user: CoreUser, index: Int): Feat576UserItem1 {
    return Feat576UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat576StateBlock(model: Feat576UiModel): Feat576StateBlock1 {
    return Feat576StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat576UserSummary> {
    val list = java.util.ArrayList<Feat576UserSummary>(users.size)
    for (user in users) {
        list += Feat576UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat576UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat576UiModel {
    val summaries = (0 until count).map {
        Feat576UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat576UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat576UiModel> {
    val models = java.util.ArrayList<Feat576UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat576AnalyticsEvent1(val name: String, val value: String)
data class Feat576AnalyticsEvent2(val name: String, val value: String)
data class Feat576AnalyticsEvent3(val name: String, val value: String)
data class Feat576AnalyticsEvent4(val name: String, val value: String)
data class Feat576AnalyticsEvent5(val name: String, val value: String)
data class Feat576AnalyticsEvent6(val name: String, val value: String)
data class Feat576AnalyticsEvent7(val name: String, val value: String)
data class Feat576AnalyticsEvent8(val name: String, val value: String)
data class Feat576AnalyticsEvent9(val name: String, val value: String)
data class Feat576AnalyticsEvent10(val name: String, val value: String)

fun logFeat576Event1(event: Feat576AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat576Event2(event: Feat576AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat576Event3(event: Feat576AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat576Event4(event: Feat576AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat576Event5(event: Feat576AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat576Event6(event: Feat576AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat576Event7(event: Feat576AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat576Event8(event: Feat576AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat576Event9(event: Feat576AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat576Event10(event: Feat576AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat576Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat576Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat576Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat576Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat576Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat576Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat576Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat576Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat576Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat576Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat576(u: CoreUser): Feat576Projection1 =
    Feat576Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat576Projection1> {
    val list = java.util.ArrayList<Feat576Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat576(u)
    }
    return list
}
