package com.romix.feature.feat230

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat230Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat230UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat230FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat230UserSummary
)

data class Feat230UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat230NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat230Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat230Config = Feat230Config()
) {

    fun loadSnapshot(userId: Long): Feat230NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat230NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat230UserSummary {
        return Feat230UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat230FeedItem> {
        val result = java.util.ArrayList<Feat230FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat230FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat230UiMapper {

    fun mapToUi(model: List<Feat230FeedItem>): Feat230UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat230UiModel(
            header = UiText("Feat230 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat230UiModel =
        Feat230UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat230UiModel =
        Feat230UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat230UiModel =
        Feat230UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat230Service(
    private val repository: Feat230Repository,
    private val uiMapper: Feat230UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat230UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat230UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat230UserItem1(val user: CoreUser, val label: String)
data class Feat230UserItem2(val user: CoreUser, val label: String)
data class Feat230UserItem3(val user: CoreUser, val label: String)
data class Feat230UserItem4(val user: CoreUser, val label: String)
data class Feat230UserItem5(val user: CoreUser, val label: String)
data class Feat230UserItem6(val user: CoreUser, val label: String)
data class Feat230UserItem7(val user: CoreUser, val label: String)
data class Feat230UserItem8(val user: CoreUser, val label: String)
data class Feat230UserItem9(val user: CoreUser, val label: String)
data class Feat230UserItem10(val user: CoreUser, val label: String)

data class Feat230StateBlock1(val state: Feat230UiModel, val checksum: Int)
data class Feat230StateBlock2(val state: Feat230UiModel, val checksum: Int)
data class Feat230StateBlock3(val state: Feat230UiModel, val checksum: Int)
data class Feat230StateBlock4(val state: Feat230UiModel, val checksum: Int)
data class Feat230StateBlock5(val state: Feat230UiModel, val checksum: Int)
data class Feat230StateBlock6(val state: Feat230UiModel, val checksum: Int)
data class Feat230StateBlock7(val state: Feat230UiModel, val checksum: Int)
data class Feat230StateBlock8(val state: Feat230UiModel, val checksum: Int)
data class Feat230StateBlock9(val state: Feat230UiModel, val checksum: Int)
data class Feat230StateBlock10(val state: Feat230UiModel, val checksum: Int)

fun buildFeat230UserItem(user: CoreUser, index: Int): Feat230UserItem1 {
    return Feat230UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat230StateBlock(model: Feat230UiModel): Feat230StateBlock1 {
    return Feat230StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat230UserSummary> {
    val list = java.util.ArrayList<Feat230UserSummary>(users.size)
    for (user in users) {
        list += Feat230UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat230UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat230UiModel {
    val summaries = (0 until count).map {
        Feat230UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat230UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat230UiModel> {
    val models = java.util.ArrayList<Feat230UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat230AnalyticsEvent1(val name: String, val value: String)
data class Feat230AnalyticsEvent2(val name: String, val value: String)
data class Feat230AnalyticsEvent3(val name: String, val value: String)
data class Feat230AnalyticsEvent4(val name: String, val value: String)
data class Feat230AnalyticsEvent5(val name: String, val value: String)
data class Feat230AnalyticsEvent6(val name: String, val value: String)
data class Feat230AnalyticsEvent7(val name: String, val value: String)
data class Feat230AnalyticsEvent8(val name: String, val value: String)
data class Feat230AnalyticsEvent9(val name: String, val value: String)
data class Feat230AnalyticsEvent10(val name: String, val value: String)

fun logFeat230Event1(event: Feat230AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat230Event2(event: Feat230AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat230Event3(event: Feat230AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat230Event4(event: Feat230AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat230Event5(event: Feat230AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat230Event6(event: Feat230AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat230Event7(event: Feat230AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat230Event8(event: Feat230AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat230Event9(event: Feat230AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat230Event10(event: Feat230AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat230Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat230Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat230Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat230Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat230Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat230Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat230Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat230Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat230Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat230Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat230(u: CoreUser): Feat230Projection1 =
    Feat230Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat230Projection1> {
    val list = java.util.ArrayList<Feat230Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat230(u)
    }
    return list
}
