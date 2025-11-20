package com.romix.feature.feat248

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat248Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat248UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat248FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat248UserSummary
)

data class Feat248UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat248NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat248Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat248Config = Feat248Config()
) {

    fun loadSnapshot(userId: Long): Feat248NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat248NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat248UserSummary {
        return Feat248UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat248FeedItem> {
        val result = java.util.ArrayList<Feat248FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat248FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat248UiMapper {

    fun mapToUi(model: List<Feat248FeedItem>): Feat248UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat248UiModel(
            header = UiText("Feat248 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat248UiModel =
        Feat248UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat248UiModel =
        Feat248UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat248UiModel =
        Feat248UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat248Service(
    private val repository: Feat248Repository,
    private val uiMapper: Feat248UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat248UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat248UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat248UserItem1(val user: CoreUser, val label: String)
data class Feat248UserItem2(val user: CoreUser, val label: String)
data class Feat248UserItem3(val user: CoreUser, val label: String)
data class Feat248UserItem4(val user: CoreUser, val label: String)
data class Feat248UserItem5(val user: CoreUser, val label: String)
data class Feat248UserItem6(val user: CoreUser, val label: String)
data class Feat248UserItem7(val user: CoreUser, val label: String)
data class Feat248UserItem8(val user: CoreUser, val label: String)
data class Feat248UserItem9(val user: CoreUser, val label: String)
data class Feat248UserItem10(val user: CoreUser, val label: String)

data class Feat248StateBlock1(val state: Feat248UiModel, val checksum: Int)
data class Feat248StateBlock2(val state: Feat248UiModel, val checksum: Int)
data class Feat248StateBlock3(val state: Feat248UiModel, val checksum: Int)
data class Feat248StateBlock4(val state: Feat248UiModel, val checksum: Int)
data class Feat248StateBlock5(val state: Feat248UiModel, val checksum: Int)
data class Feat248StateBlock6(val state: Feat248UiModel, val checksum: Int)
data class Feat248StateBlock7(val state: Feat248UiModel, val checksum: Int)
data class Feat248StateBlock8(val state: Feat248UiModel, val checksum: Int)
data class Feat248StateBlock9(val state: Feat248UiModel, val checksum: Int)
data class Feat248StateBlock10(val state: Feat248UiModel, val checksum: Int)

fun buildFeat248UserItem(user: CoreUser, index: Int): Feat248UserItem1 {
    return Feat248UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat248StateBlock(model: Feat248UiModel): Feat248StateBlock1 {
    return Feat248StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat248UserSummary> {
    val list = java.util.ArrayList<Feat248UserSummary>(users.size)
    for (user in users) {
        list += Feat248UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat248UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat248UiModel {
    val summaries = (0 until count).map {
        Feat248UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat248UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat248UiModel> {
    val models = java.util.ArrayList<Feat248UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat248AnalyticsEvent1(val name: String, val value: String)
data class Feat248AnalyticsEvent2(val name: String, val value: String)
data class Feat248AnalyticsEvent3(val name: String, val value: String)
data class Feat248AnalyticsEvent4(val name: String, val value: String)
data class Feat248AnalyticsEvent5(val name: String, val value: String)
data class Feat248AnalyticsEvent6(val name: String, val value: String)
data class Feat248AnalyticsEvent7(val name: String, val value: String)
data class Feat248AnalyticsEvent8(val name: String, val value: String)
data class Feat248AnalyticsEvent9(val name: String, val value: String)
data class Feat248AnalyticsEvent10(val name: String, val value: String)

fun logFeat248Event1(event: Feat248AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat248Event2(event: Feat248AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat248Event3(event: Feat248AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat248Event4(event: Feat248AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat248Event5(event: Feat248AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat248Event6(event: Feat248AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat248Event7(event: Feat248AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat248Event8(event: Feat248AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat248Event9(event: Feat248AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat248Event10(event: Feat248AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat248Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat248Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat248Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat248Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat248Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat248Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat248Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat248Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat248Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat248Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat248(u: CoreUser): Feat248Projection1 =
    Feat248Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat248Projection1> {
    val list = java.util.ArrayList<Feat248Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat248(u)
    }
    return list
}
